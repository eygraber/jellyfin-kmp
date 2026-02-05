---
paths:
  - "ui/**/*.kt"
  - "**/icons/**/*.kt"
---

# Icons
🔴 CRITICAL: Never use androidx.compose.material.icons - use JellyfinIcons only
❌ Bad: import androidx.compose.material.icons.Icons
✅ Good: import com.eygraber.jellyfin.ui.icons.JellyfinIcons

Add new icons to ui/icons module as ImageVector extensions on JellyfinIcons
Use Valkyrie IDE plugin to convert SVG to Compose ImageVector

# Images
Use Coil for image loading and display in Jetpack Compose
Previews of composables that use Coil should wrap content in JellyfinPreviewAsyncImageProvider
Images should be optimized for mobile display
Use vectors (using materialIcon preferably, or XML) when possible for scalability

# Resources
Shared resources (strings, drawables) are in the common module
Provide appropriate content descriptions for accessibility

# Theming
Follow Material Design 3 guidelines for all UI components
Use Material 3 theming with brand color palettes
Support dynamic theming based on branding configuration

### Documentation Reference
For complete patterns: .docs/compose/
