# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [1.0.0-beta3] 2020-02-01

### Changes
- Update to lifecycle-viewmodel 2.2.0
- Add kotlin viewModels extension
- Update fragments to 1.2.0
- Update kotlin to 1.3.61
- Update material to 1.1.0-rc02 (for compatibility with lifecycle-viewmodel)
- Update viewpager2 to 1.0.0

### Fixes
- Fix memory leaks
- Fix erroneous firing of OnNavigationItemReselectedListener when using ShardPageHostUI

## Breaking Changes
- Removed ViewModelProviders in favor of the ViewModelProvider constructor
- Rename getCompositLayoutInflater to getCompositeLayoutInflater

## [1.0.0-beta2] 2019-10-16

### Changes
- Deps updated to the latest stable versions, now only the viewpager2 artifact is still in beta.

### Breaking Changes
- Changes to the viewpager2 adapter to better match the androidx one:
  - ShardPagerAdapter renamed to ShardAdapter
  - getItem renamed to createShard
