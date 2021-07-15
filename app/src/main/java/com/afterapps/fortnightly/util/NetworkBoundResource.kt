package com.afterapps.fortnightly.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = channelFlow {
    val data = query().first()

    if (shouldFetch(data)) {
        val loading = launch {
            query().collect { send(Resource.Loading(data = it)) }
        }

        try {
            saveFetchResult(fetch())
            loading.cancel()
            query().collect { send(Resource.Success(data = it)) }
        } catch (t: Throwable) {
            loading.cancel()
            query().collect { send(Resource.Error(data = it, throwable = t)) }
        }
    } else {
        query().collect { send(Resource.Success(data = it)) }
    }
}