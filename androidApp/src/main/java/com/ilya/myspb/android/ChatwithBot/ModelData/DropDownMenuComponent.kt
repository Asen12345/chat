package com.larkes.hsesurvey.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.larkes.hsesurvey.domain.models.CategoryTypes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenuComponent(
    modifier: Modifier = Modifier,
    selectedTitle:CategoryTypes,
    onSelect:(CategoryTypes) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        Row {
            OutlinedTextField(
                value = when(selectedTitle.name){
                            CategoryTypes.CityLife.name -> { "Благоустройство, ЖКХ и уборка дорог" }
                            CategoryTypes.SearchContacts.name -> { "Поиск контактов, основанный на Базе Контактов Санкт-Петербурга" }
                            CategoryTypes.SearchRelevantInfo.name -> { "Поиск релевантной информации, ответ на вопрос основанный на Базе Знаний Санкт-Петербурга" }
                            CategoryTypes.Education.name -> { "Образование, Детские сады и Школы" }
                            CategoryTypes.Trash.name -> { "Раздельный сбор" }
                            CategoryTypes.Fun.name -> { "Афиша + Красивые места" }
                              else -> {"Выберете категорию"}
                },
                onValueChange = {

                },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = modifier.menuAnchor(),
            )

        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
        ) {
            CategoryTypes.entries.forEach{ entry ->
                Text(
                    text = when(entry.name){
                        CategoryTypes.CityLife.name -> { "Благоустройство, ЖКХ и уборка дорог" }
                        CategoryTypes.SearchContacts.name -> { "Поиск контактов, основанный на Базе Контактов Санкт-Петербурга" }
                        CategoryTypes.SearchRelevantInfo.name -> { "Поиск релевантной информации, ответ на вопрос основанный на Базе Знаний Санкт-Петербурга" }
                        CategoryTypes.Education.name -> { "Образование, Детские сады и Школы" }
                        CategoryTypes.Trash.name -> { "Раздельный сбор" }
                        CategoryTypes.Fun.name -> { "Афиша + Красивые места" }
                        else -> {"None"}
                    },
                    modifier = Modifier.clickable {
                        onSelect(entry)
                        expanded = false
                    }.padding(vertical = 10.dp, horizontal = 15.dp)
                )
            }
        }
    }
}