let context;
let enabled = true;
export function setSound(value) { enabled = value; if (!value && context) context.suspend().catch(() => {}); }
export function suspendSound() { if (context) context.suspend().catch(() => {}); }
export function sound(type = 'place') {
  if (!enabled) return;
  try {
    const Audio = globalThis.AudioContext || globalThis.webkitAudioContext;
    if (!Audio) return;
    context ||= new Audio();
    context.resume().catch(() => {});
    const notes = {place:[330,440],remove:[330,220],step:[150],win:[523,659,784,1047],lose:[294,247,196],hint:[660,880],click:[440]};
    (notes[type] || notes.click).forEach((frequency,index) => {
      const oscillator = context.createOscillator();
      const gain = context.createGain();
      const start = context.currentTime + index * .095;
      oscillator.type = type === 'step' ? 'sine' : 'triangle';
      oscillator.frequency.setValueAtTime(frequency,start);
      gain.gain.setValueAtTime(0,start);
      gain.gain.linearRampToValueAtTime(type === 'step' ? .018 : .055,start + .012);
      gain.gain.exponentialRampToValueAtTime(.001,start + .16);
      oscillator.connect(gain).connect(context.destination);
      oscillator.start(start); oscillator.stop(start + .18);
    });
  } catch { /* Audio is optional, including restricted WebViews. */ }
}
