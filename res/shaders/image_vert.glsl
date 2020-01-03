#version 330 core

layout(location = 0) in vec2 position;

out vec2 uv;

void main ()
{
	uv = position;
    gl_Position = vec4((position - 0.5f) * 2, 0f, 1.0f);
}