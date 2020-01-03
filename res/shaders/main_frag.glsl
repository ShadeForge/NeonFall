#version 330 core

out vec4 color;

in vec2 texCoords;

uniform vec4 diffuse;
uniform sampler2D sampler;

void main(void){
    color = diffuse * texture2D(sampler, texCoords);
}