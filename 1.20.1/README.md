# Mob Loot Bags — Minecraft 1.20.1

Fabric and Forge (47) workspace for Minecraft 1.20.1: `common/`, `fabric/` and `forge/`. Shared product docs live in the [repository root README](../README.md).

## Requirements

- JDK 21 to run Gradle, plus JDK 17 for the toolchain (the jars are Java 17 bytecode)
- Open this `1.20.1/` folder as the Gradle project root, or build every workspace with `./gradlew buildAll` from the
  repository root

## Build

```bash
./gradlew build          # fabric/build/libs and forge/build/libs: mob_loot_bags-{fabric,forge}-1.20.1-<version>.jar
./gradlew :common:test
./gradlew :fabric:runClient
./gradlew :forge:runClient
```

## License

Apache License 2.0 — see [../LICENSE](../LICENSE) and [../NOTICE](../NOTICE).
