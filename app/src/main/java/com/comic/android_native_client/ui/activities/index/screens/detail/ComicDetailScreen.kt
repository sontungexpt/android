package com.comic.android_native_client.ui.activities.index.screens.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.comic.android_native_client.R
import com.comic.android_native_client.constants.Screen
import com.comic.android_native_client.ui.activities.index.screens.favorite.FavoriteViewModel
import com.comic.android_native_client.ui.components.ChapterCard
import com.comic.android_native_client.ui.components.common.ExpandableText
import com.comic.android_native_client.ui.components.common.LoadingCircle
import com.comic.android_native_client.ui.components.layout.BackFloatingScreen
import com.comic.android_native_client.ui.utils.formatTimeAgo
import com.comic.android_native_client.ui.utils.stripHtmlTags


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComicDetailScreen(
    favoriteViewModel: FavoriteViewModel,
    comicDetailViewModel: ComicDetailViewModel,
    horizontalPadding: Dp = 16.dp,
    navController: NavController,
    currentComic: Screen.ComicDetail,
) {

    val uiState by comicDetailViewModel.comicDetailUIState.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(currentComic.id) {
        comicDetailViewModel.initialize(
            currentComic.id,
            currentComic.sourceName
        )
    }

    BackFloatingScreen(
        backButtonModifier = Modifier.offset(x = 24.dp, y = 24.dp),
        onBackCLick = {
            navController.popBackStack()
        },
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(
                    horizontal = horizontalPadding,
                    vertical = 20.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(
                contentType = "header_section",
                key = "header_section"
            ) {
                ComicDetailHeader(
                    comicName = currentComic.name ?: uiState.comicDetail?.name ?: "",
                    comicImageUrl = currentComic.imageUrl,
                    onGenreClick = { name, index ->
                        if (uiState.comicDetail != null) {
                            navController.navigate(
                                Screen.ComicByCategory(
                                    id = uiState.comicDetail!!.categories[index].id,
                                    name = name
                                )
                            )
                        }

                    },
                    comicGenres = uiState.comicDetail?.categories?.map {
                        it.name
                    } ?: currentComic.genres ?: emptyList(),
                )
            }
            stickyHeader(
                contentType = "reading_button_section",
                key = "reading_button_section"
            ) {
                Surface(
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            navController.navigate(
                                Screen.ComicReading(
                                    chapterId = "",
                                    comicId = currentComic.id,
                                    lastestRead = true
                                )
                            )
                        },
                        enabled = uiState.chapters.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = stringResource(R.string.continue_reading),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item(
                contentType = "comic_stats_section",
                key = "comic_stats_section"
            ) {
                ComicStatsSection(
                    views = 100,
                    rating = "200",
                    favorited = uiState.comicDetail?.followed ?: false,
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth(),
                    favoriteEnabled = uiState.comicDetail != null,
                    favoritedChanging = favoriteViewModel.favoriteChanging,
                    onToggleFavorited = {
                        comicDetailViewModel.updateFavoriteStatus(
                            it,
                            { comic, onSuccess ->
                                favoriteViewModel.favoriteComic(
                                    comic,
                                    onSuccess
                                )
                            },
                            { comic, onSuccess ->
                                favoriteViewModel.unfavoriteComic(
                                    comic,
                                    onSuccess
                                )
                            }
                        )
                    }
                )
            }

            item(
                contentType = "comic_information_section",
                key = "comic_information_section"
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = stringResource(R.string.introduction)
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    val onUpdating = stringResource(R.string.on_updating)

                    var authorsString =
                        uiState.comicDetail?.authors?.map { it.name }?.joinToString(", ")
                    if (authorsString.isNullOrBlank()) {
                        authorsString = stringResource(R.string.on_updating)
                    }

                    Text(
                        text = stringResource(R.string.authors) + ": " + authorsString,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    var artistsString =
                        uiState.comicDetail?.artists?.map { it.name }?.joinToString(", ")
                    if (artistsString.isNullOrBlank()) {
                        artistsString = stringResource(R.string.on_updating)
                    }

                    Text(
                        text = stringResource(R.string.artists) + ": " + artistsString,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val status = uiState.comicDetail?.status ?: onUpdating
                    Text(
                        text = stringResource(R.string.status) + ": " + status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val source = uiState.comicDetail?.originalSource
                    var sourceName = if (source != null) {
                        var result = source.name
                        if (source.link.isNotBlank()) {
                            result + " (" + source.link + ")"
                        } else {
                            result
                        }
                    } else onUpdating

                    Text(
                        text = stringResource(R.string.source) + ": " + sourceName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val alternativeNames = uiState.comicDetail?.alternativeNames?.joinToString(", ")
                    if (!alternativeNames.isNullOrBlank()
                    ) {
                        Text(
                            text = stringResource(R.string.alternative_names) + ": " + alternativeNames,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            item(
                contentType = "comic_summary_section",
                key = "comic_summary_section"
            ) {
                Text(
                    text = stringResource(R.string.summary),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (uiState.initializing) {
                    LoadingCircle(
                        wrapperModifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    ExpandableText(
                        text = stripHtmlTags(uiState.comicDetail?.summary ?: ""),
                        textAlign = TextAlign.Justify,
                        collapsedMaxLines = 5
                    )
                }

            }


            item(
                contentType = "comic_chapter_title_section",
                key = "comic_chapter_title_section"
            ) {
                SectionDivider()
                Text(
                    text = stringResource(R.string.chapter_list),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(
                items = uiState.chapters,
                key = { it.id },
                contentType = { it.javaClass }
            ) {
                ChapterCard(
                    onClick = {
                        navController.navigate(
                            Screen.ComicReading(
                                chapterId = it.id,
                                comicId = currentComic.id,
                                lastestRead = false
                            )
                        )
                    },
                    isRead = it.read,
                    name = it.name,
                    number = it.num.toString(),
                    imageUrl = if (it.thumbnailUrl.isNotBlank()) {
                        it.thumbnailUrl
                    } else {
                        currentComic.imageUrl
                    },
                    updateDate = if (it.updatedDate != null) {
                        formatTimeAgo(
                            context = navController.context,
                            dateAgo = it.updatedDate
                        )
                    } else {
                        stringResource(R.string.on_updating)
                    },
                )
            }

            if (comicDetailViewModel.noMoreChapter) return@LazyColumn
            item(
                contentType = "loading_more_chapter",
                key = "loading_more_chapter"
            ) {
                LaunchedEffect(true) {
                    if (!uiState.loadingMoreChapter) {
                        comicDetailViewModel.loadMoreChapter()
                    }
                }

                LoadingCircle(
                    loading = uiState.loadingMoreChapter,
                    wrapperModifier = Modifier
                        .height(32.dp)
                        .fillMaxWidth(),
                    modifier = Modifier.size(28.dp),
                    color = MaterialTheme.colorScheme.primary
                )

            }

        }
    }


}



