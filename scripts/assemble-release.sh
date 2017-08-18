#!/usr/bin/env bash
# USAGE: ./assemble-release.sh <password>

if [ $# -eq 0 ]
  then
    echo "USAGE: ./assemble-release.sh <password>"
   exit
fi

# 1: Decrypt signing configs & keystore
PASSWORD=$1
openssl aes-256-cbc -d -k "$PASSWORD" -in app/src/release/signing-configs.gradle.enc -out app/src/release/signing-configs.gradle
openssl aes-256-cbc -d -k "$PASSWORD" -in app/src/release/release-keystore.jks.enc -out app/src/release/release-keystore.jks

# 2: Assemble signed release with gradle
./gradlew :app:assembleRelease

# 3: Remove decrypted files
rm app/src/release/signing-configs.gradle
rm app/src/release/release-keystore.jks

# 4: Restore dummy signing configs
echo \
"android {
    signingConfigs {
        release {
            storeFile file(\"src/release/release-keystore.jks\")
            storePassword \"encrypted\"
            keyAlias \"encrypted\"
            keyPassword \"encrypted\"
        }
    }
}" > app/src/release/signing-configs.gradle