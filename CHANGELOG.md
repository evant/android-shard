# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [1.0.0-beta2] 2019-10-16

### Changes
- Deps updated to the latest stable versions, now only the viewpager2 artifact is still in beta.

### Breaking Changes
- Changes to the viewpager2 adapter to better match the support one:
  - ShardPagerAdapter renamed to ShardAdapter
  - getItem renamed to createShard
