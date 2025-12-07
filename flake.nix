{
  description = "MoodTracker Android App";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    android-nixpkgs = {
      url = "github:tadfisher/android-nixpkgs";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = { self, nixpkgs, flake-utils, android-nixpkgs }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config = {
            android_sdk.accept_license = true;
            allowUnfree = true;
          };
        };

        # Android SDK configuration using android-nixpkgs
        androidSdk = android-nixpkgs.sdk.${system} (sdkPkgs: with sdkPkgs; [
          cmdline-tools-latest
          build-tools-35-0-0
          platform-tools
          platforms-android-36
          platforms-android-35
        ]);

      in
      {
        # Development shell with Android SDK and tools
        devShells.default = pkgs.mkShell {
          buildInputs = [
            androidSdk
            pkgs.jdk17
            pkgs.gradle
          ];

          shellHook = ''
            export ANDROID_HOME="${androidSdk}/share/android-sdk"
            export ANDROID_SDK_ROOT="$ANDROID_HOME"
            export JAVA_HOME="${pkgs.jdk17.home}"
            export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/35.0.0:$JAVA_HOME/bin:$PATH"

            echo "╔══════════════════════════════════════════════════════════════╗"
            echo "║  MoodTracker - Android Build Environment                    ║"
            echo "╚══════════════════════════════════════════════════════════════╝"
            echo ""
            echo "Environment:"
            echo "  ANDROID_HOME: $ANDROID_HOME"
            echo "  JAVA_HOME: $JAVA_HOME"
            echo ""
            echo "Build commands:"
            echo "  ./gradlew assembleDebug    - Build debug APK"
            echo "  ./gradlew assembleRelease  - Build release APK"
            echo ""
          '';
        };
      }
    );
}
