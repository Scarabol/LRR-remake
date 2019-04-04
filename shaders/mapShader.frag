#version 400 core

in vec3 mPos;
in vec3 mNorm;
in vec2 mTexPos;
in vec4 mWave;
in float mt;

out vec4 outColor;

uniform mat4 cam;
uniform mat4 mapTrans;
uniform sampler2D tex;
uniform float texRot;
uniform float ambient;
uniform bool lines;
uniform vec3 lightDirect = normalize(vec3(-1,-1,-1));
uniform float lightDirectI = 0.3;
uniform vec3 lightPoint = vec3(40*8+20, 180, 40*9+20);
uniform float lightPointI = 1;

float dist2(vec3 v1, vec3 v2) {
	vec3 t = v1-v2;
	return (t.x*t.x + t.y*t.y + t.z*t.z);
}

void main() {
	
	vec4 color = texture(tex, mTexPos);
	
	mat3 normalMatrix = transpose(inverse(mat3(mapTrans)));
	vec3 tNorm = normalize(normalMatrix * mNorm);
	
	float d2p = dist2(mPos, lightPoint)/lightPointI;
	float a = 1.0/(1.0 + pow(d2p, 2));
	float Kd = dot(lightDirect, tNorm) / (length(lightDirect)*length(tNorm));
	Kd = clamp(Kd, 0.0, 1.0);
	
	vec3 L = mPos - lightPoint;
	float Kp = dot(L, tNorm) / (length(L)/length(tNorm));
//	Kp = 1;
	
	float point = clamp(a, 0, 1);
	
	if(!lines)
		outColor = vec4((ambient*color + point*color + lightDirectI*Kd*color).xyz, color.w);
	else
		outColor = vec4(1,0,0,1);
}