{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "profile.choosed" -}}
{{- if .Values.profile.withPosgtre -}}
{{- "prod" -}}
{{- else -}}
{{- "dev" -}}
{{- end -}}
{{- end -}}