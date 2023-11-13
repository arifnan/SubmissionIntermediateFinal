package com.example.submissionintermediate.auth

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.submissionintermediate.data.api.ApiService
import com.example.submissionintermediate.data.response.Story
import kotlinx.coroutines.delay

class StoryPagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int, Story>() {

    private companion object {
        const val INTIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: INTIAL_PAGE_INDEX
            if(page > 1) delay(1000) //Delay 1 Sec to see loading
            val responseData =
                apiService.getStories(token = "Bearer $token", page = page, size = params.loadSize)
            if (responseData.isSuccessful) {
                responseData.body()?.let {
                    LoadResult.Page(
                        data = it.listStory,
                        prevKey = if (page == INTIAL_PAGE_INDEX) null else page - 1,
                        nextKey = if (it.listStory.isEmpty()) null else page + 1
                    )
                } ?: LoadResult.Error(Exception("Empty response body"))
            } else {
                LoadResult.Error(Exception("Network request failed"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}