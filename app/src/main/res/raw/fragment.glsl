precision mediump float;

uniform sampler2D uTextureSampler;
uniform vec3 uAmbientLight;

varying vec2 vTexture;
varying vec3 vModelViewVertexNormal;
varying vec3 vModelViewVertexPos;

void main() {
    vec4 diffuseC = texture2D(uTextureSampler, vTexture);
    gl_FragColor = diffuseC * vec4(uAmbientLight, 1.0);
}