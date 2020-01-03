#version 330 core

out vec4 color;

in vec2 uv;

uniform sampler2D renderedSampler;
uniform sampler2D glowSampler;

void main ()
{
	vec4 rendered = texture2D(renderedSampler, uv);
	vec4 glow = texture2D(glowSampler, uv);
	color = clamp((glow*2 + rendered) - (glow * rendered), 0.0, 1.0);

}