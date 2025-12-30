#version 150

uniform vec4 ColorModulator;

uniform vec2 u_Size;
uniform float u_Radius;
uniform float u_BorderWidth;
uniform vec4 u_BorderColor;

// Gradient colors: TopLeft, TopRight, BottomLeft, BottomRight
uniform vec4 u_ColorTL;
uniform vec4 u_ColorTR;
uniform vec4 u_ColorBL;
uniform vec4 u_ColorBR;

uniform int u_GradientType; // 0 = 4-corner, 1 = Angle
uniform float u_Angle;      // In radians

in vec2 texCoord; // (0,0) - top left, (1,1) - bottom right
out vec4 fragColor;

float sdRoundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    vec2 pos = (texCoord - 0.5) * u_Size;
    vec2 halfSize = u_Size * 0.5;

    /*
        SDF Shape and Stroke (same logic)
    */
    float dist = sdRoundedBox(pos, halfSize, u_Radius);
    float alpha = 1.0 - smoothstep(0.0, 1.0, dist);
    if (alpha <= 0.0) discard;

    /*
        Calculating the fill color
    */
    vec4 fillColor;

    if (u_GradientType == 1) {
        /*
            --- LINEAR ANGLE GRADIENT ---
            Calculate the direction vector (0 rad = right, PI/2 = down)
        */
        vec2 dir = vec2(cos(u_Angle), sin(u_Angle));

        /*
            Projecting a point onto a direction vector
        */
        float proj = dot(pos, dir);

        /*
            We calculate the maximum projection length inside this box,
            to normalize the gradient exactly from edge to edge
        */
        float maxDist = abs(halfSize.x * dir.x) + abs(halfSize.y * dir.y);

        /*
            Convert to the range 0..1
        */
        float t = 0.5 + (proj / (2.0 * maxDist));

        fillColor = mix(u_ColorTL, u_ColorBR, clamp(t, 0.0, 1.0));
    } else {
        /*
            --- 4-POINT LINEAR (Old) ---
        */
        vec4 topColor = mix(u_ColorTL, u_ColorTR, texCoord.x);
        vec4 bottomColor = mix(u_ColorBL, u_ColorBR, texCoord.x);
        fillColor = mix(topColor, bottomColor, texCoord.y);
    }

    /*
        Mixing with the border
    */
    float borderFactor = smoothstep(-u_BorderWidth - 1.0, -u_BorderWidth, dist);
    vec4 finalColor = mix(fillColor, u_BorderColor, borderFactor);

    finalColor.a *= alpha;
    fragColor = finalColor * ColorModulator;
}