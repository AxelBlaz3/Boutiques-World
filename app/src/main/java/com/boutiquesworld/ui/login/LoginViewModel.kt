package com.boutiquesworld.ui.login

import androidx.lifecycle.ViewModel
import com.boutiquesworld.repository.RetailerRepository
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class LoginViewModel(private val retailerRepository: RetailerRepository) : ViewModel() {}