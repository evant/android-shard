# Transition Animations

## Creating a ShardTransition

Animations from one shard to another are handled with a `ShardTransition`. You can create one from
an `Animation`

```kotlin
val transition = ShardTransition.fromAnimations(enterAnimation, exitAnimation)
```

, `Animator`

```kotlin
val transition = ShardTransition.fromAnimators(enterAnimator, exitAnimator)
```

, or `Transition`. 

```kotlin
val transition = ShardTransition.fromTransition(transition)
```

You can also crete one from their respective resources.

```kotlin
val animTransition = ShardTransition.fromAnimRes(context, R.anim.enter, R.anim.exit)
val animatorTransition = ShardTransition.fromAnimRes(context, R.animator.enter, R.animator.exit)
val transitionTransition = ShardTransition.fromTransitioNRes(context, R.transition.transition)
```

For transitions, you can use the compat version of transitions with `shard-transition`.

```kotlin
val transition = ShardTransitionCompat.fromTransition(transition)
val transitionRes = ShardTransitionCompat.fromTransitionRes(context, R.transition.transition)
```

## ShardHost/ShardPageHost

You mostly deal with providing transitions to various hosts. For example, both `ShardHost` and
`ShardPageHost` allow you to pass an optional `ShardTransition` when setting the shard.

```kotlin
host.setShard(shard, shardTransition)
pageHost.setCurrentPage(R.id.page1, shardTransition)
```

You can also set a default transition with `setDefaultTransition()`. This will be run for every 
shard set.

```kotlin
host.defaultTransition = shardTransition
host.shard = MyShard()

pageHost.defaultTransition = shardTransition
pageHost.currentPage = R.id.page1
```

## ShardNavHost

Because `ShardNavHost` needs to save it's state and for compatibility with the graph xml, you can't
set a `ShardTransition` directly. Instead, you can use the `app:enterAnim`, `app:exitAnim`, etc. 
to set animation/animator resources 

```xml
<action
    android:id="@+id/to_shard2"
    app:destination="@id/shard2"
    app:enterAnim="@anim/fade_in"
    app:exitAnim="@anim/fade_out" />
```

or you can set a transition resource through extras.

```kotlin
controler.navigate(R.id.to_dest, null, null, ShardNavigator.Extras.Builder()
    .transition(R.transition.my_transition)
    .build())
```
