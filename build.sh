#!/bin/bash
set -e

echo "📦 Installing dependencies..."
apt-get update -qq
apt-get install -y -qq openjdk-17-jdk wget unzip

echo "📦 Downloading Android SDK..."
wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
unzip -q commandlinetools-linux-9477386_latest.zip -d android-sdk
rm commandlinetools-linux-9477386_latest.zip

echo "📦 Setting up SDK..."
mkdir -p android-sdk/cmdline-tools
mv android-sdk/cmdline-tools android-sdk/latest
mv android-sdk/latest android-sdk/cmdline-tools/

export ANDROID_SDK_ROOT=/app/android-sdk
export PATH=$PATH:/app/android-sdk/cmdline-tools/bin

echo "📦 Accepting licenses..."
yes | sdkmanager --sdk_root=/app/android-sdk --licenses || true

echo "📦 Installing platforms..."
sdkmanager --sdk_root=/app/android-sdk "platforms;android-34" "build-tools;34.0.0" || true

echo "📦 Building APK..."
chmod +x gradlew
./gradlew assembleRelease --no-daemon || true

echo "✅ Done!"
