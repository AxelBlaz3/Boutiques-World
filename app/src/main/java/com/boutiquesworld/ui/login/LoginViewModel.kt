package com.boutiquesworld.ui.login

import androidx.lifecycle.ViewModel
import com.boutiquesworld.repository.RetailerRepository
import javax.inject.Singleton

@Singleton
class LoginViewModel(private val retailerRepository: RetailerRepository) : ViewModel() {}