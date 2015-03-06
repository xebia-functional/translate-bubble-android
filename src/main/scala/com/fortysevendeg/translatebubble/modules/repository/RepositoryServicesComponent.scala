package com.fortysevendeg.translatebubble.modules.repository

import com.fortysevendeg.translatebubble.service.Service

trait RepositoryServices {
  def addTranslationHistory: Service[AddTranslationHistoryRequest, AddTranslationHistoryResponse]
  def fetchAllTranslationHistory: Service[FetchAllTranslationHistoryRequest, FetchAllTranslationHistoryResponse]
}

trait RepositoryServicesComponent {
  val repositoryServices: RepositoryServices
}
