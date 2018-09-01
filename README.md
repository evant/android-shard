# better-fragment
[WIP] 'Fragments' with a simpler api built on top of the android architecture components

```kotlin
class MyFragment: Fragment() {
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        // set the layouts
        setContentView(R.layout.my_fragment)
        // get a ViewModel
        val vm = ViewModelProviders.of(this)[MyViewModel::class.java]
        // listen with LiveData
        vm.name.observe(this, Observer { value -> ... })
    }
    
    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        // save state
    }
}
```
