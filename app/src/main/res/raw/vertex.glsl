uniform mat4 uProjectionMatrix;
uniform mat4 uWorldMatrix;

attribute vec3 aPosition; // per-vertex position
attribute vec3 aColor; // per-vertex color

varying vec3 vColor; // will be passed to the fragment shader

void main()
{
    vColor = aColor;
    gl_Position = uProjectionMatrix * uWorldMatrix * vec4(aPosition, 1);
}