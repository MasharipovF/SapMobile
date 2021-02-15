package com.example.sapmobile.utils

import androidx.paging.PagingSource
import com.example.sapmobile.models.Items

import com.example.sapmobile.services.ItemsService

class ItemsPagingSource (
    private val service: ItemsService,
    private val searchValue:String
): PagingSource<Int, Items>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Items> {
        TODO("Not yet implemented")
    }
}