
package com.sohailcreations.happyequals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sohailcreations.happyequals.R

@Composable
fun WelcomeScreen() {

    val deepNavy = Color(0xFF071226)
    val navyBlue = Color(0xFF0C2650)
    val cyan = Color(0xFF30EAF4)
    val white = Color(0xFFF5FBFF)
    val muted = Color(0xFFA9BDD2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        navyBlue,
                        deepNavy,
                        Color(0xFF050D1C)
                    )
                )
            )
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // App branding
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.logo
                    ),
                    contentDescription = "Happy Equals Logo",
                    modifier = Modifier.size(58.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column {

                    Text(
                        text = "Happy Equals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = white
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "SCIENTIFIC CALCULATOR",
                        fontSize = 10.sp,
                        letterSpacing = 1.4.sp,
                        color = cyan
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(65.dp)
            )

            // Main heading
            Text(
                text = "A little joy in\nevery calculation.",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    lineHeight = 42.sp
                ),
                fontWeight = FontWeight.Bold,
                color = white
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Text(
                text = "A smoother way to calculate, explore and find your answers.",
                style = MaterialTheme.typography.bodyLarge,
                color = muted,
                lineHeight = 25.sp
            )

            Spacer(
                modifier = Modifier.height(44.dp)
            )

            // Feature preview
            val cardShape = RoundedCornerShape(24.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF173A61),
                                Color(0xFF112641)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = cyan.copy(alpha = 0.20f),
                        shape = cardShape
                    )
                    .padding(23.dp)
            ) {

                Text(
                    text = "YOUR WORKSPACE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = cyan
                )

                Spacer(
                    modifier = Modifier.height(17.dp)
                )

                Text(
                    text = "Scientific calculations",
                    style = MaterialTheme.typography.titleLarge,
                    color = white
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                Text(
                    text = "Powerful calculations, an interactive What-if Mode and a history of your answers.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = muted,
                    lineHeight = 23.sp
                )
            }
        }

        // Footer
        Text(
            text = "BY SOHAIL CREATIONS",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp),
            color = muted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.6.sp,
            textAlign = TextAlign.Center
        )
    }
}