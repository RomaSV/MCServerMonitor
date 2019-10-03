uniform mat4 uProjectionMatrix;
uniform mat4 uModelViewMatrix;

attribute vec3 aPosition; // per-vertex position
attribute vec2 aTexture; // per-vertex texture coords
attribute vec3 aVertexNormal;

varying vec2 vTexture; // will be passed to the fragment shader
varying vec3 vModelViewVertexNormal;
varying vec3 vModelViewVertexPos;

void main()
{
    vec4 modelViewPos = uModelViewMatrix * vec4(aPosition, 1.0);
    gl_Position = uProjectionMatrix * modelViewPos;

    vTexture = aTexture;
    // do not want normal to be translated, so 4th coord is 0
    vModelViewVertexNormal = normalize(uModelViewMatrix * vec4(aVertexNormal, 0.0)).xyz;
    vModelViewVertexPos = modelViewPos.xyz;
}