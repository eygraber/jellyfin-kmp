Never commit secrets or API keys to version control
Use ejson for sensitive configuration (requires /etc/ejson/keys with proper key)
Secrets are encrypted in *.ejson files and decrypted at build time
Request only necessary Android permissions
Be mindful of PII and sensitive user data in all layers
Use HTTPS for all API calls
Validate and sanitize all user inputs
Regularly update dependencies to patch vulnerabilities (check gradle/libs.versions.toml)

### Documentation Reference
For complete patterns: .docs/workflow/setup.md
