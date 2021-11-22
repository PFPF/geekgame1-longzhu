A = 31793, B = 32479; // 0x7c31, 0x7edf from type-2 Longzhu
a = 2604, b = -2549, l = []; // magic number: A * 2604 - B * 2549 = 1;
for(let i=0; i<=34; i++) {
  l.push([a,b]);
  a *= 2, b *= 2;         // we want A * l[i][0] + B * l[i][1] = 2^i
  a %= B, b %= A;
}
// l = [[2604,-2549],[5208,-5098],[10416,-10196],[20832,-20392],[9185,-8991],[18370,-17982],[4261,-4171],[8522,-8342],[17044,-16684],[1609,-1575],[3218,-3150],[6436,-6300],[12872,-12600],[25744,-25200],[19009,-18607],[5539,-5421],[11078,-10842],[22156,-21684],[11833,-11575],[23666,-23150],[14853,-14507],[29706,-29014],[26933,-26235],[21387,-20677],[10295,-9561],[20590,-19122],[8701,-6451],[17402,-12902],[2325,-25804],[4650,-19815],[9300,-7837],[18600,-15674],[4721,-31348],[9442,-30903],[18884,-30013]]
data = Array(100).fill(-1);
function send(x32, y32, cf) {
  xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() { if (this.readyState == 4 && this.status == 200) cf(JSON.parse(this.responseText).materials); };
  xhttp.open("GET", "api/state?token=617%3AMEQCIEx7GKmjHkB%2F9SaAeZYufJWzSVQK%2FjsOPy%2B9oLXaatmxAiBdnW0NW0rqgvGZ%2BUeLl9S%2FkcDg7YIEwcT0zLOJU%2FWzDg%3D%3D&x=" + x32 + "&y=" + y32, true);
  xhttp.send(); 
}
function getData(x, y, index, P = 53, goal = "SECOND") {
  x *= P, y *= P;
  for(let x32 = x - (x + 32000000) % 32; x32 < x + P; x32+=32) for(let y32 = y - (y + 32000000) % 32; y32 < y + P; y32+=32) {
    send(x32, y32, function(mat) {
      for(let i = 0; i < 32; i++) for(let j=0; j<32; j++) if(mat[i][j] == goal && x32+i >= x && x32+i < x+P && y32+j >= y && y32+j < y+P) {
        data[index] = (x32+i + P*1000000) % P;
      }
    });
  }
}
function prepare() {
  getData(0, 0, 99);
  for(let i=0; i<=34; i++) getData(l[i][0], l[i][1], i*2), getData(-l[i][0], -l[i][1], i*2+1); // predict
  for(let i=70; i<95; i++) getData((i*155683)^6272861, i*i+1, i); // narrow
  for(let i=95; i<99; i++) getData((i*72123)^1359851, (i*i*i)^1087415, i, 193, "THIRD"); // aim
}
prepare();
// wait a while, then JSON.stringify(data) and copy to java
