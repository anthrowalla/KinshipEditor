// Canvas rendering: symbols, lines, labels
// Port of Kind.java drawing + KinEditPanel.paint()

import { state, SEX_FEMALE, SEX_MALE, SEX_NEUTER } from './model.js';

// Draw a filled circle (Female symbol)
function drawCircle(ctx, b, color) {
  ctx.fillStyle = color;
  ctx.beginPath();
  ctx.ellipse(b.x + b.width / 2, b.y + b.height / 2, b.width / 2, b.height / 2, 0, 0, Math.PI * 2);
  ctx.fill();
}

// Draw a filled triangle (Male symbol)
function drawTriangle(ctx, b, color) {
  ctx.fillStyle = color;
  ctx.beginPath();
  ctx.moveTo(b.x + b.width / 2, b.y);
  ctx.lineTo(b.x, b.y + b.height);
  ctx.lineTo(b.x + b.width, b.y + b.height);
  ctx.closePath();
  ctx.fill();
}

// Draw a filled square (Neuter symbol)
function drawSquare(ctx, b, color) {
  ctx.fillStyle = color;
  ctx.fillRect(b.x, b.y, b.width, b.height);
}

// Draw equals sign (Marriage/Union symbol)
function drawEquals(ctx, b, color) {
  const yh = 4;
  const yo = yh + 4;
  const y = b.y + 4;
  ctx.fillStyle = color;
  ctx.fillRect(b.x, y, b.width, yh);
  ctx.fillRect(b.x, y + yo, b.width, yh);
}

// Draw death/end overlay (diagonal bar)
function drawEndOverlay(ctx, b) {
  ctx.fillStyle = '#000';
  ctx.beginPath();
  ctx.moveTo(b.x, b.y - 3);
  ctx.lineTo(b.x + 2, b.y - 3);
  ctx.lineTo(b.x + b.width, b.y + b.height + 5);
  ctx.lineTo(b.x + b.width - 2, b.y + b.height + 5);
  ctx.closePath();
  ctx.fill();
}

// Draw a person's symbol
export function drawPersonSymbol(ctx, person, color) {
  const b = person.bounds();
  const drawFn = person.sex === SEX_FEMALE ? drawCircle
               : person.sex === SEX_MALE ? drawTriangle
               : drawSquare;

  person.drawn = false;

  if (person.hasEnded()) {
    drawFn(ctx, b, color);
    drawEndOverlay(ctx, b);
    drawPersonLabel(ctx, person, b);
    person.drawn = true;
  } else if (person.hasBegun()) {
    drawFn(ctx, b, color);
    drawPersonLabel(ctx, person, b);
    person.drawn = true;
  }
}

// Draw a person's label
function drawPersonLabel(ctx, person, b) {
  const label = person.getLabel();
  if (!label) return;
  ctx.fillStyle = '#000';
  ctx.font = '11px sans-serif';
  const w = ctx.measureText(label).width;
  const x = b.x + b.width / 2 - w / 2;
  const y = b.y + b.height + 14;
  ctx.fillText(label, x, y);
}

// Draw a marriage symbol
export function drawMarriageSymbol(ctx, marriage, color) {
  const b = marriage.bounds();
  marriage.drawn = false;

  if (marriage.hasEnded()) {
    drawEquals(ctx, b, color);
    drawEndOverlay(ctx, b);
    marriage.drawn = true;
  } else if (marriage.hasBegun()) {
    drawEquals(ctx, b, color);
    marriage.drawn = true;
  }
}

// Draw spouse connecting lines for a marriage
// Returns { midx, midy } for connecting sibling lines
export function drawSpouseLines(ctx, marriage) {
  const b = marriage.bounds();
  const xw = b.width / 2;
  const xh = b.height;
  const sp = marriage.spouses;
  let midx = marriage.location.x;
  let midy = marriage.location.y + xh + 4;

  if (!marriage.hasBegun()) return { midx, midy };

  if (sp.length > 0) {
    let minx = marriage.location.x;
    let maxx = marriage.location.x;
    let miny = 9999, maxy = -9999;

    for (const p of sp) {
      if (p.location.x < minx) minx = p.location.x;
      if (p.location.x > maxx) maxx = p.location.x;
      if (p.location.y < miny) miny = p.location.y;
      if (p.location.y > maxy) maxy = p.location.y;
    }

    maxy += xh + 4;
    minx += xw;
    maxx += xw;

    ctx.strokeStyle = '#000';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(minx, maxy);
    ctx.lineTo(maxx, maxy);
    ctx.stroke();

    midx = minx + (maxx - minx) / 2;
    midy = maxy;

    for (const p of sp) {
      ctx.beginPath();
      ctx.moveTo(p.location.x + xw, p.location.y + xh);
      ctx.lineTo(p.location.x + xw, maxy);
      ctx.stroke();
    }
  }

  return { midx, midy };
}

// Draw sibling connecting lines
export function drawSibLines(ctx, marriage, midx, midy) {
  const b = marriage.bounds();
  const xw = b.width / 2;
  const sp = marriage.sibset;

  if (!marriage.hasBegun()) return;
  if (sp.length === 0) return;

  let minx = marriage.location.x;
  let maxx = marriage.location.x;
  let count = 0;

  for (const p of sp) {
    if (p.location.x !== -100 && p.location.y !== -100 && p.hasBegun()) {
      if (p.location.x < minx) minx = p.location.x;
      if (p.location.x > maxx) maxx = p.location.x;
      count++;
    }
  }

  if (count === 0) return;

  const miny = Math.min(...sp.filter(p => p.location.x !== -100 && p.location.y !== -100 && p.hasBegun()).map(p => p.location.y)) - 5;
  minx += xw;
  maxx += xw;

  ctx.strokeStyle = '#000';
  ctx.lineWidth = 1;

  // Horizontal line across siblings
  ctx.beginPath();
  ctx.moveTo(minx, miny);
  ctx.lineTo(maxx, miny);
  ctx.stroke();

  // Vertical line from parent link to sibling line
  ctx.beginPath();
  ctx.moveTo(marriage.location.x + xw, miny);
  ctx.lineTo(marriage.location.x + xw, midy);
  ctx.stroke();

  // Vertical lines down to each sibling
  for (const p of sp) {
    if (p.location.x !== -100 && p.location.y !== -100 && p.hasBegun()) {
      ctx.beginPath();
      ctx.moveTo(p.location.x + xw, p.location.y);
      ctx.lineTo(p.location.x + xw, miny);
      ctx.stroke();
    }
  }
}

// Draw marriage lines (spouse + sibling)
export function drawMarriageLines(ctx, marriage) {
  const { midx, midy } = drawSpouseLines(ctx, marriage);
  drawSibLines(ctx, marriage, midx, midy);
}

// Draw a selection/drag line
export function drawSelectLine(ctx, fromP, toP) {
  ctx.strokeStyle = '#ccc';
  ctx.lineWidth = 1;
  ctx.beginPath();
  ctx.moveTo(fromP.x, fromP.y);
  ctx.lineTo(toP.x, toP.y);
  ctx.stroke();
}

// Draw relationship drop target indicators (green=spouse, magenta=child)
export function drawDropTarget(ctx, marriage, toP) {
  const b = marriage.bounds();
  const expandedBounds = {
    x: b.x,
    y: b.y - b.height / 2,
    width: b.width,
    height: b.height * 2,
  };

  if (toP.x >= expandedBounds.x && toP.x <= expandedBounds.x + expandedBounds.width &&
      toP.y >= expandedBounds.y && toP.y <= expandedBounds.y + expandedBounds.height) {
    const midY = expandedBounds.y + expandedBounds.height / 2;
    if (toP.y < midY) {
      // Top half - spouse indicator (green)
      ctx.fillStyle = 'rgba(0, 128, 0, 0.4)';
      ctx.beginPath();
      ctx.ellipse(b.x + b.width / 2, expandedBounds.y + expandedBounds.height / 4,
                  b.width / 2, expandedBounds.height / 4, 0, 0, Math.PI * 2);
      ctx.fill();
      return 1; // spouse half
    } else {
      // Bottom half - sibling indicator (magenta)
      ctx.fillStyle = 'rgba(255, 0, 255, 0.4)';
      ctx.beginPath();
      ctx.ellipse(b.x + b.width / 2, expandedBounds.y + expandedBounds.height * 3 / 4,
                  b.width / 2, expandedBounds.height / 4, 0, 0, Math.PI * 2);
      ctx.fill();
      return 2; // sibling half
    }
  }
  return -1;
}

// Full render of the kinship diagram
export function render(ctx, canvasWidth, canvasHeight, editorState) {
  const { originX, originY, whichFolk, whichKnot, selectLine } = editorState;

  ctx.clearRect(0, 0, canvasWidth, canvasHeight);
  ctx.save();
  ctx.translate(originX, originY);

  const viewRect = {
    x: -originX,
    y: -originY,
    width: canvasWidth,
    height: canvasHeight,
  };

  // Draw persons
  for (let i = state.folkIndex; i >= 0; i--) {
    const p = state.folks[i];
    if (!p) continue;
    const color = (i === whichFolk) ? '#ff0000' : '#000000';
    drawPersonSymbol(ctx, p, color);
  }

  // Draw marriages (symbols + lines)
  let tiedKnot = -1;
  let whichHalf = -1;

  for (let i = state.knotIndex; i >= 0; i--) {
    const m = state.knots[i];
    if (!m) continue;
    const color = (i === whichKnot) ? '#ff0000' : '#000000';
    drawMarriageSymbol(ctx, m, color);
    drawMarriageLines(ctx, m);

    // Check for drop target if dragging
    if (selectLine) {
      const half = drawDropTarget(ctx, m, selectLine.toP);
      if (half > 0) {
        tiedKnot = i;
        whichHalf = half;
      }
    }
  }

  // Draw selection line
  if (selectLine) {
    drawSelectLine(ctx, selectLine.fromP, selectLine.toP);
  }

  ctx.restore();
  return { tiedKnot, whichHalf };
}
