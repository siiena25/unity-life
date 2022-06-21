package utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.llc.aceplace_ru.ext.put
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FragmentArgumentDelegate<T : Any> : ReadWriteProperty<Fragment, T> {
    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val args = thisRef.arguments ?: Bundle().also(thisRef::setArguments)
        val key = property.name
        args.put(key, value)
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val key = property.name
        return thisRef.arguments?.get(key) as? T
                ?: throw IllegalStateException("Property ${property.name} not supported")
    }
}