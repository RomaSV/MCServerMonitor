uniform mat4 uProjectionMatrix;
uniform mat4 uModelViewMatrix;

attribute vec3 aPosition; // per-vertex position
attribute vec2 aTexture; // per-vertex texture coords

varying vec2 vTexture; // will be passed to the fragment shader
varying vec3 vModelViewVertexPos;

void main()
{
    vec4 modelViewPos = uModelViewMatrix * vec4(aPosition, 1.0);
    gl_Position = uProjectionMatrix * modelViewPos;

    vTexture = aTexture;
    vModelViewVertexPos = modelViewPos.xyz;
}