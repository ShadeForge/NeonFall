
#version 330 core
out vec4 color;
in vec2 uv;

uniform sampler2D sampler;

uniform int orientation;
uniform int blurAmount;
uniform float blurScale;
uniform float blurStrength;

uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

float Gaussian (float x, float deviation)
{
	return (1.0 / sqrt(2.0 * 3.141592 * deviation)) * exp(-((x * x) / (2.0 * deviation)));
}

void main()
{
    vec2 tex_offset = 1.0 / textureSize(sampler, 0); // gets mapSize of single texel
    vec3 result = texture(sampler, uv).rgb * weight[0]; // current fragment's contribution

    if(orientation == 0)
    {
        for(int i = 1; i < 5; ++i)
        {
            result += texture(sampler, uv + vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
            result += texture(sampler, uv - vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
        }
    }
    else
    {
        for(int i = 1; i < 5; ++i)
        {
            result += texture(sampler, uv + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
            result += texture(sampler, uv - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
        }
    }
    color = vec4(result, 1.0);
}