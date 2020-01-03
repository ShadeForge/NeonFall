#version 330 core

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 texCoordinates;

out vec2 texCoords;

void main ()
{
	texCoords = texCoordinates;
    gl_Position = vec4(position, 0f, 1.0f);
}