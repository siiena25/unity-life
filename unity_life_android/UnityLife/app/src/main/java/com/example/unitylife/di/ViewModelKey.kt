package com.llc.aceplace_ru.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import java.lang.annotation.Documented
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)