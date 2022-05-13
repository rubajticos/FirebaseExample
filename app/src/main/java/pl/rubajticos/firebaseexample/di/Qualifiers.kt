package pl.rubajticos.firebaseexample.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SignInRequest

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SignUpRequest