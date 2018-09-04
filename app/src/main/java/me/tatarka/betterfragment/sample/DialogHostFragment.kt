package me.tatarka.betterfragment.sample

import android.view.View
import me.tatarka.betterfragment.app.Fragment
import me.tatarka.betterfragment.app.FragmentDialogHost
import me.tatarka.betterfragment.sample.dagger.DaggerFragmentFactory
import javax.inject.Inject

class DialogHostFragment @Inject constructor(private val fragmentFactory: DaggerFragmentFactory) :
    Fragment() {

    private val dialogHost: FragmentDialogHost = FragmentDialogHost(this, fragmentFactory)

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.dialogs)
        requireViewById<View>(R.id.simple_dialog).setOnClickListener {
            dialogHost.show(
                fragmentFactory.newInstance<SimpleDialogFragment>()
                    .withNumber(1)
            )
        }
        requireViewById<View>(R.id.alert_dialog).setOnClickListener {
            dialogHost.show(fragmentFactory.newInstance<MyAlertDialogFragment>())
        }
        requireViewById<View>(R.id.alert_dialog_custom_view).setOnClickListener {
            dialogHost.show(
                fragmentFactory.newInstance<MyAlertDialogFragment>()
                    .withCustomView(2)
            )
        }
        stateStore.addStateSaver()
    }
}