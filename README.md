# Config

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Installation](#installation)
4. [Examples](#examples)

---

## Overview

Config is a configuration and serialization library for Java.

It makes reading and writing data to and from different data formats as simple as possible.

---

## Features

- Read and write JSON, YAML, and possibly other formats in the future
- Public Domain. No restrictions, use it however you like.

---

## Installation

#### See the repo-tags for the version id's

### Maven

```xml

<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.lunatic-gh</groupId>
    <artifactId>config</artifactId>
    <version>VERSION</version>
  </dependency>
</dependencies>


```

### Gradle

```
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.lunatic-gh:config:VERSION'
}
```

## Examples

For usage examples, see the "examples" directory.
