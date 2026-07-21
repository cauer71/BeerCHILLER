
# Keep AndroidX's official edge-to-edge compatibility implementation in its
# own namespace. Otherwise R8 can inline its legacy, API-gated Window calls
# into an app Activity and Google Play attributes those calls to the app.
-keep class androidx.activity.EdgeToEdge** { *; }
