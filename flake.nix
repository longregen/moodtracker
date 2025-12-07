{
  description = "MoodTracker Android App";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config = {
            android_sdk.accept_license = true;
            allowUnfree = true;
          };
        };

        # Extract build configuration from gradle files
        gradleConfig = let
          buildGradle = builtins.readFile ./app/build.gradle.kts;
          versionsCatalog = builtins.readFile ./gradle/libs.versions.toml;

          # Extract compileSdk from build.gradle.kts
          compileSdkMatch = builtins.match ".*compileSdk = ([0-9]+).*" buildGradle;
          compileSdk = if compileSdkMatch != null then builtins.head compileSdkMatch else "36";

          # Extract targetSdk from build.gradle.kts
          targetSdkMatch = builtins.match ".*targetSdk = ([0-9]+).*" buildGradle;
          targetSdk = if targetSdkMatch != null then builtins.head targetSdkMatch else "35";

          # Extract AGP version from libs.versions.toml
          agpMatch = builtins.match ".*agp = \"([0-9.]+)\".*" versionsCatalog;
          agpVersion = if agpMatch != null then builtins.head agpMatch else "8.10.1";

          # Map AGP version to build tools version
          # AGP 8.x requires build-tools 34.0.0 or higher
          buildToolsVersion = "35.0.0";
        in {
          inherit compileSdk targetSdk buildToolsVersion;
        };

        androidComposition = pkgs.androidenv.composeAndroidPackages {
          buildToolsVersions = [ gradleConfig.buildToolsVersion ];
          platformVersions = [ gradleConfig.compileSdk gradleConfig.targetSdk ];
          abiVersions = [ "armeabi-v7a" "arm64-v8a" "x86" "x86_64" ];
          includeNDK = false;
          includeSystemImages = false;
        };

        androidSdk = androidComposition.androidsdk;

        buildEnv = {
          ANDROID_HOME = "${androidSdk}/libexec/android-sdk";
          ANDROID_SDK_ROOT = "${androidSdk}/libexec/android-sdk";
          GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${androidSdk}/libexec/android-sdk/build-tools/${gradleConfig.buildToolsVersion}/aapt2";
        };

      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            androidSdk
            jdk17
            gradle
          ];
          inherit (buildEnv) ANDROID_HOME ANDROID_SDK_ROOT GRADLE_OPTS;
        };

        packages.debugApk = pkgs.stdenv.mkDerivation (finalAttrs: {
          pname = "moodtracker";
          version = "1.0";

          src = ./.;

          nativeBuildInputs = with pkgs; [
            androidSdk
            jdk17
            gradle_8
          ];

          inherit (buildEnv) ANDROID_HOME ANDROID_SDK_ROOT GRADLE_OPTS;

          # Signal to Gradle that this is a Nix build to avoid test configurations
          NIX_BUILD = "1";

          # Specify the Gradle task to build - this helps fetchDeps know what to resolve
          gradleBuildTask = "assembleDebug";

          # TODO: Enable once deps.json works with Android test configuration issues
          # mitmCache = pkgs.gradle_8.fetchDeps {
          #   pkg = finalAttrs.finalPackage;
          #   data = ./deps.json;
          # };

          # Allow network access for Gradle to download dependencies
          # This is needed until we resolve the androidTest configuration ambiguity in fetchDeps
          __darwinAllowLocalNetworking = true;
          __noChroot = true;

          preConfigure = ''
            # Create a local SDK directory that Gradle can detect
            export ANDROID_USER_HOME=$PWD/.android
            mkdir -p $ANDROID_USER_HOME

            # Create local.properties pointing to the Nix Android SDK
            cat > local.properties <<EOF
            sdk.dir=$ANDROID_HOME
            EOF

            # Remove androidTest directory to prevent Gradle from creating test configurations
            # that cause ambiguity errors with fetchDeps
            rm -rf app/src/androidTest || true
          '';

          buildPhase = ''
            runHook preBuild

            gradle --offline --no-daemon ${finalAttrs.gradleBuildTask}

            runHook postBuild
          '';

          installPhase = ''
            runHook preInstall

            mkdir -p $out
            cp app/build/outputs/apk/debug/*.apk $out/

            runHook postInstall
          '';
        });

        packages.default = self.packages.${system}.debugApk;
      }
    );
}
