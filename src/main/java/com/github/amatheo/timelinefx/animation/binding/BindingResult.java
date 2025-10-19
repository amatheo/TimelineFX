package com.github.amatheo.timelinefx.animation.binding;

import com.github.amatheo.timelinefx.transform.Transform;

import java.util.Map;

public record BindingResult(Transform transform, Map<String, Object> parameters) {}
