package com.example.ecommerceapp.presentation.Utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect as LaunchedEffect1
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import coil.compose.AsyncImage
import com.example.ecommerceapp.R
import com.example.ecommerceapp.domain.models.BannerDataModels
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale


@Composable
fun SelectDot(modifier: Modifier) {

    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(5.dp))
            .padding(2.dp)
            .height(10.dp)
            .width(28.dp)
            .background(
                colorResource(id = R.color.teal_200).copy(alpha = 0.8f),
                shape = RoundedCornerShape(5.dp)
            )

    )
}

@Composable
fun IndicatorDot(isSelected: Boolean, modifier: Modifier) {
    if (isSelected) {
        SelectDot(modifier)
    } else {
        Box(
            modifier = modifier
                .clip(shape = CircleShape)
                .padding(2.dp)
                .size(8.dp)
                .background(
                    colorResource(id = R.color.teal_200).copy(alpha = 0.5f),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun PageIndicator(pageCount: Int, CurrentPage: Int, modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.padding(top = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) {
            IndicatorDot(isSelected = it == CurrentPage, modifier)
        }

    }
}

@Composable
fun Banner(banners: List<BannerDataModels>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val scope = rememberCoroutineScope()
    LaunchedEffect1(Unit) {
        while (true) {
            delay(1500)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            scope.launch {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.wrapContentSize()

            ) { currentPage ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 15.dp, end = 15.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    AsyncImage(
                        model = banners[currentPage].image,
                        contentDescription = banners[currentPage].name,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center

                    )
                }

            }
        }
        PageIndicator(
            pageCount = banners.size,
            CurrentPage = pagerState.currentPage,
            modifier = Modifier
        )
    }
}