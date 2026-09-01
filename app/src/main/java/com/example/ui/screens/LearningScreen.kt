package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.RequestType
import com.example.data.CodingChallenge
import com.example.data.LearningCatalog
import com.example.data.LearningLesson
import com.example.ui.AppNavDestination
import com.example.ui.AppViewModel
import com.example.ui.components.CodeBlockView
import com.example.ui.components.CyberHeader
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDarkEmerald
import com.example.ui.theme.CyberPanelBg
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TerminalAmber
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LearningScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Tracks & Lessons, 1: Coding Challenges
    var selectedTrackId by remember { mutableStateOf("python") }

    val selectedLesson by viewModel.selectedLesson.collectAsState()
    val selectedChallenge by viewModel.selectedChallenge.collectAsState()
    val progressList by viewModel.learningProgress.collectAsState()
    val experienceLevel by viewModel.preferencesManager.experienceLevel.collectAsState()
    val activeProvider by viewModel.preferencesManager.activeProvider.collectAsState()
    val fontSizeSp by viewModel.preferencesManager.codeFontSize.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = CyberBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            CyberHeader(
                statusText = "● CODING MENTOR ACTIVE",
                experienceLevel = experienceLevel.title,
                activeProviderName = activeProvider.displayName
            )

            // Tabs: Learning Tracks | Coding Challenges
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CyberPanelBg,
                contentColor = NeonGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NeonGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        viewModel.setSelectedLesson(null)
                    },
                    text = {
                        Text(
                            "LEARNING TRACKS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        viewModel.setSelectedChallenge(null)
                    },
                    text = {
                        Text(
                            "CHALLENGES & DRILLS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
            }

            if (selectedTabIndex == 0) {
                // TRACKS & LESSONS VIEW
                if (selectedLesson != null) {
                    LessonDetailView(
                        lesson = selectedLesson!!,
                        isCompleted = progressList.any { it.lessonId == selectedLesson!!.id && it.isCompleted },
                        fontSizeSp = fontSizeSp,
                        onBack = { viewModel.setSelectedLesson(null) },
                        onMarkComplete = {
                            viewModel.markLessonCompleted(selectedLesson!!.id, selectedLesson!!.trackId)
                            Toast.makeText(context, "Lesson completed! +100 XP", Toast.LENGTH_SHORT).show()
                        },
                        onCopyCode = { viewModel.copyToClipboard(it) },
                        onAskAI = { prompt ->
                            viewModel.createNewSession(prompt) {
                                viewModel.navigateTo(AppNavDestination.CHAT)
                            }
                        }
                    )
                } else {
                    TracksListView(
                        selectedTrackId = selectedTrackId,
                        onSelectTrack = { selectedTrackId = it },
                        lessons = LearningCatalog.lessons.filter { it.trackId == selectedTrackId },
                        progressList = progressList,
                        onSelectLesson = { viewModel.setSelectedLesson(it) }
                    )
                }
            } else {
                // CHALLENGES VIEW
                if (selectedChallenge != null) {
                    ChallengeDetailView(
                        challenge = selectedChallenge!!,
                        fontSizeSp = fontSizeSp,
                        onBack = { viewModel.setSelectedChallenge(null) },
                        onCopyCode = { viewModel.copyToClipboard(it) },
                        onAskAIReview = { userCode ->
                            viewModel.createNewSession("Review this solution for challenge '${selectedChallenge!!.title}':\n```\n$userCode\n```") {
                                viewModel.navigateTo(AppNavDestination.CHAT)
                            }
                        }
                    )
                } else {
                    ChallengesListView(
                        challenges = LearningCatalog.challenges,
                        onSelectChallenge = { viewModel.setSelectedChallenge(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun TracksListView(
    selectedTrackId: String,
    onSelectTrack: (String) -> Unit,
    lessons: List<LearningLesson>,
    progressList: List<com.example.data.models.LearningProgressEntity>,
    onSelectLesson: (LearningLesson) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "DEVELOPER ROADMAPS",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = NeonGreen
        )
        Text(
            text = "Curated step-by-step master tracks designed for beginners and intermediate developers.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Track Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LearningCatalog.tracks.forEach { (id, name) ->
                val isSelected = selectedTrackId == id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberDarkEmerald else CyberCardBg)
                        .border(1.dp, if (isSelected) NeonGreen else CyberBorder, RoundedCornerShape(8.dp))
                        .clickable { onSelectTrack(id) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) NeonGreen else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TRACK LESSONS",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (lessons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCardBg)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Interactive lessons for this track are loaded dynamically.\nAsk the AI Mentor anytime!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            lessons.forEach { lesson ->
                val isCompleted = progressList.any { it.lessonId == lesson.id && it.isCompleted }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberCardBg)
                        .border(1.dp, if (isCompleted) EmeraldGreen.copy(alpha = 0.6f) else CyberBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectLesson(lesson) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                if (isCompleted) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = NeonGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lesson.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "⏱ ${lesson.estimatedMinutes} mins",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TerminalAmber
                                )
                                Text(
                                    text = "Language: ${lesson.language.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TerminalCyan
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonDetailView(
    lesson: LearningLesson,
    isCompleted: Boolean,
    fontSizeSp: Int,
    onBack: () -> Unit,
    onMarkComplete: () -> Unit,
    onCopyCode: (String) -> Unit,
    onAskAI: (String) -> Unit
) {
    var showHint by remember { mutableStateOf(false) }
    var showSolution by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Back Button + Lesson Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonGreen)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Concept Explanation Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CyberPanelBg)
                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CONCEPT THEORY", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = NeonGreen)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = lesson.conceptExplanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("CODE WALKTHROUGH", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))

        CodeBlockView(
            code = lesson.codeExample,
            language = lesson.language,
            fontSizeSp = fontSizeSp,
            onCopy = { onCopyCode(lesson.codeExample) },
            onExplain = { onAskAI("Explain this code example from lesson '${lesson.title}':\n```\n${lesson.codeExample}\n```") }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Practice Challenge Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CyberCardBg)
                .border(1.dp, TerminalCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Quiz, contentDescription = null, tint = TerminalCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PRACTICE DRILL", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TerminalCyan)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = lesson.practiceChallenge,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showHint = !showHint },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCardElevated, contentColor = TerminalAmber),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showHint) "HIDE HINT" else "NEED A HINT?", style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = { showSolution = !showSolution },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberDarkEmerald, contentColor = NeonGreen),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showSolution) "HIDE SOLUTION" else "REVEAL SOLUTION", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (showHint) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💡 Hint: ${lesson.hint}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TerminalAmber
                    )
                }

                if (showSolution) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CodeBlockView(
                        code = lesson.solutionCode,
                        language = lesson.language,
                        fontSizeSp = fontSizeSp,
                        onCopy = { onCopyCode(lesson.solutionCode) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Complete & AI Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onAskAI("Give me more practice problems and explain deeply about '${lesson.title}' in ${lesson.language}.") },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCardElevated, contentColor = TerminalCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ASK AI MENTOR", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }

            Button(
                onClick = onMarkComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) CyberDarkEmerald else NeonGreen,
                    contentColor = if (isCompleted) NeonGreen else Color(0xFF001A0D)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isCompleted) "COMPLETED" else "MARK COMPLETE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun ChallengesListView(
    challenges: List<CodingChallenge>,
    onSelectChallenge: (CodingChallenge) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "ALGORITHM CHALLENGES",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = NeonGreen
        )
        Text(
            text = "Real-world interview problems & data structures from Beginner to Advanced.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            challenges.forEach { challenge ->
                val diffColor = when (challenge.difficulty) {
                    "BEGINNER" -> NeonGreen
                    "EASY" -> EmeraldGreen
                    "INTERMEDIATE" -> TerminalAmber
                    else -> AlertRed
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberCardBg)
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectChallenge(challenge) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = challenge.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(diffColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = challenge.difficulty,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = diffColor
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = challenge.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeDetailView(
    challenge: CodingChallenge,
    fontSizeSp: Int,
    onBack: () -> Unit,
    onCopyCode: (String) -> Unit,
    onAskAIReview: (String) -> Unit
) {
    var scratchpadCode by remember { mutableStateOf(challenge.starterCode) }
    var showSolution by remember { mutableStateOf(false) }
    var activeHintIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonGreen)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Description Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CyberPanelBg)
                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(text = challenge.description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Example Input: ${challenge.exampleInput}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = TerminalCyan)
                Text(text = "Example Output: ${challenge.exampleOutput}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = NeonGreen)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("YOUR CODE SCRATCHPAD", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = scratchpadCode,
            onValueChange = { scratchpadCode = it },
            textStyle = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp + 6).sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = CyberBlack,
                unfocusedContainerColor = CyberBlack
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Hints & Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    activeHintIndex = (activeHintIndex + 1) % challenge.hints.size
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCardElevated, contentColor = TerminalAmber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("HINT (${activeHintIndex + 1}/${challenge.hints.size})", style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = { onAskAIReview(scratchpadCode) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberDarkEmerald, contentColor = NeonGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("AI REVIEW", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }

        if (activeHintIndex >= 0 && activeHintIndex < challenge.hints.size) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "💡 Hint ${activeHintIndex + 1}: ${challenge.hints[activeHintIndex]}",
                style = MaterialTheme.typography.bodySmall,
                color = TerminalAmber
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Reveal Solution Accordion
        Button(
            onClick = { showSolution = !showSolution },
            colors = ButtonDefaults.buttonColors(containerColor = CyberCardBg, contentColor = TerminalCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showSolution) "HIDE OFFICIAL SOLUTION" else "REVEAL OFFICIAL SOLUTION & EXPLANATION")
        }

        if (showSolution) {
            Spacer(modifier = Modifier.height(10.dp))
            CodeBlockView(
                code = challenge.solutionCode,
                language = challenge.language,
                fontSizeSp = fontSizeSp,
                onCopy = { onCopyCode(challenge.solutionCode) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = challenge.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
