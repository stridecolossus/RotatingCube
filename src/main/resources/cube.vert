#version 450

layout(binding = 1) uniform UniformBuffer {
    mat4 matrix;
} ubo;

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inTexCoord;

layout(location = 0) out vec2 outTexCoord;

void main() {
    //gl_Position = proj * view * model * vec4(inPosition, 1.0);
    gl_Position = ubo.matrix * vec4(inPosition, 1.0);
    outTexCoord = inTexCoord;
}
