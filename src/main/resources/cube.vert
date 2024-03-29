#version 450

layout(set=0, binding=1) uniform Matrices {
    mat4 projection;
    mat4 view;
    mat4[16] model;
};

layout(location=0) in vec3 inPosition;
layout(location=1) in vec3 inNormal;
layout(location=2) in vec2 inTexCoord;

layout(location=0) out vec2 outTexCoord;

void main() {
    gl_Position = projection * view * model[gl_InstanceIndex] * vec4(inPosition, 1.0);
    outTexCoord = inTexCoord;
}
