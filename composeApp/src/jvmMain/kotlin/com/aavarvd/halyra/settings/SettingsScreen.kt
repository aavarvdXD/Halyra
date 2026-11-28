package com.aavarvd.halyra.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aavarvd.halyra.ui.AppColors
import com.aavarvd.halyra.ui.AppFonts
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    state: SettingsState,
    onClose: () -> Unit,
) {
    val categories = SettingsCategory.values()
        .filter {
            it.title.contains(state.searchQuery, ignoreCase = true) ||
            it.group.contains(state.searchQuery, ignoreCase = true)
        }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(
            modifier = Modifier
                .width(250.dp)
                .fillMaxHeight()
                .background(Color(0xFF252525))
                .padding(12.dp)
        ) {
            Text(
                text = "Settings",
                color = AppColors.Text,
                fontFamily = AppFonts.Inter,
                fontSize = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = state::updateSearch,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search Settings", fontSize = 12.sp) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = AppColors.Text,
                    fontSize = 13.sp
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = AppColors.Text,
                    cursorColor = AppColors.Text,
                    focusedBorderColor = AppColors.Divider,
                    unfocusedBorderColor = Color(0xFF454545),
                )
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                categories
                    .groupBy { it.group }
                    .forEach { (group, items) ->
                        Text(
                            text = group.uppercase(),
                            color = Color(0xFF888888),
                            fontSize = 10.sp,
                            fontFamily = AppFonts.Inter,
                            modifier = Modifier.padding(
                                start = 8.dp,
                                top = 12.dp,
                                bottom = 6.dp,
                            )
                        )

                        items.forEach { category ->
                            val selected =
                                state.selectedCategory == category

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(
                                        if (selected) Color(0xFF3D4652)
                                        else Color.Transparent,
                                        RoundedCornerShape(5.dp)
                                    )
                                    .clickable {
                                        state.selectCategory(category)
                                    }
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 9.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.title,
                                    color = if (selected) Color.White else AppColors.Text,
                                    fontFamily = AppFonts.Inter,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
            }

            Divider(color = Color(0xFF454545))

            Text(
                text = "Halyra",
                color = Color(0xFF7777777),
                fontSize = 11.sp,
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 12.dp,
                )
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(28.dp)
        ) {
            Text(
                text = state.selectedCategory.title,
                color = AppColors.Text,
                fontFamily = AppFonts.Inter,
                fontSize = 24.sp,
            )

            Spacer(Modifier.height(20.dp))

            Divider(color = Color(0xFF454545))

            Spacer(Modifier.height(20.dp))

            Text(
                text = "This settings page hasn't been implemented yet",
                color = Color(0xFF888888),
                fontSize = 13.sp,
                fontFamily = AppFonts.Inter,
            )
        }
    }
}