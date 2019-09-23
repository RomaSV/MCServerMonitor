uniform mat4 uProjectionMatrix;
uniform mat4 uModelViewMatrix;

attribute vec3 aPosition; // per-vertex position
attribute vec2 aTexture; // per-vertex texture coords

varying vec2 vTexture; // will be passed to the fragment shader

void main()
{
    vTexture = aTexture;
    gl_Position = uProjectionMatrix * uModelViewMatrix * vec4(aPosition, 1);
}