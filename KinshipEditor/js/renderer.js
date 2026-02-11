// Canvas rendering: symbols, lines, labels
// Port of Kind.java drawing + KinEditPanel.paint()

import { state, SEX_FEMALE, SEX_MALE, SEX_NEUTER, resetBoundingBox } from './model.js';

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

// SVG rendering functions
function svgCircle(b, color) {
  const cx = b.x + b.width / 2;
  const cy = b.y + b.height / 2;
  const rx = b.width / 2;
  const ry = b.height / 2;
  return `<ellipse cx="${cx}" cy="${cy}" rx="${rx}" ry="${ry}" fill="${color}"/>`;
}

function svgTriangle(b, color) {
  const x1 = b.x + b.width / 2;
  const y1 = b.y;
  const x2 = b.x;
  const y2 = b.y + b.height;
  const x3 = b.x + b.width;
  const y3 = b.y + b.height;
  return `<polygon points="${x1},${y1} ${x2},${y2} ${x3},${y3}" fill="${color}"/>`;
}

function svgSquare(b, color) {
  return `<rect x="${b.x}" y="${b.y}" width="${b.width}" height="${b.height}" fill="${color}"/>`;
}

function svgEquals(b, color) {
  const yh = 4;
  const yo = yh + 4;
  const y = b.y + 4;
  return `<rect x="${b.x}" y="${y}" width="${b.width}" height="${yh}" fill="${color}"/>
         <rect x="${b.x}" y="${y + yo}" width="${b.width}" height="${yh}" fill="${color}"/>`;
}

function svgEndOverlay(b) {
  const points = `${b.x},${b.y - 3} ${b.x + 2},${b.y - 3} ${b.x + b.width},${b.y + b.height + 5} ${b.x + b.width - 2},${b.y + b.height + 5}`;
  return `<polygon points="${points}" fill="#000"/>`;
}

function svgPersonLabel(person, b) {
  const label = person.getLabel();
  if (!label) return '';
  const x = b.x + b.width / 2;
  const y = b.y + b.height + 14;
  return `<text x="${x}" y="${y}" text-anchor="middle" font-family="sans-serif" font-size="11" fill="#000">${escapeXml(label)}</text>`;
}

function svgPersonSymbol(person, color) {
  person.drawn = false;
  let svg = '';

  // Only draw if the person has begun (born) or has ended (died)
  if (person.hasEnded()) {
    const b = person.bounds();
    if (person.sex === SEX_FEMALE) {
      svg = svgCircle(b, color);
    } else if (person.sex === SEX_MALE) {
      svg = svgTriangle(b, color);
    } else {
      svg = svgSquare(b, color);
    }
    svg += svgEndOverlay(b);
    svg += svgPersonLabel(person, b);
    person.drawn = true;
  } else if (person.hasBegun()) {
    const b = person.bounds();
    if (person.sex === SEX_FEMALE) {
      svg = svgCircle(b, color);
    } else if (person.sex === SEX_MALE) {
      svg = svgTriangle(b, color);
    } else {
      svg = svgSquare(b, color);
    }
    svg += svgPersonLabel(person, b);
    person.drawn = true;
  }

  return svg;
}

function svgMarriageSymbol(marriage, color) {
  marriage.drawn = false;
  let svg = '';

  // Only draw if the marriage has begun or has ended
  if (marriage.hasEnded()) {
    const b = marriage.bounds();
    svg = svgEquals(b, color);
    svg += svgEndOverlay(b);
    marriage.drawn = true;
  } else if (marriage.hasBegun()) {
    const b = marriage.bounds();
    svg = svgEquals(b, color);
    marriage.drawn = true;
  }

  return svg;
}

function svgSpouseLines(marriage) {
  const b = marriage.bounds();
  const xw = b.width / 2;
  const xh = b.height;
  const sp = marriage.spouses;
  let midx = marriage.location.x;
  let midy = marriage.location.y + xh + 4;
  let svg = '';

  if (!marriage.hasBegun()) return { svg, midx, midy };

  if (sp.length > 0) {
    let minx = marriage.location.x;
    let maxx = marriage.location.x;

    for (const p of sp) {
      if (p.location.x < minx) minx = p.location.x;
      if (p.location.x > maxx) maxx = p.location.x;
    }

    const maxy = Math.max(...sp.map(p => p.location.y)) + xh + 4;
    minx += xw;
    maxx += xw;

    // Horizontal line
    svg += `<line x1="${minx}" y1="${maxy}" x2="${maxx}" y2="${maxy}" stroke="#000" stroke-width="1"/>`;

    midx = minx + (maxx - minx) / 2;
    midy = maxy;

    // Vertical lines to each spouse
    for (const p of sp) {
      svg += `<line x1="${p.location.x + xw}" y1="${p.location.y + xh}" x2="${p.location.x + xw}" y2="${maxy}" stroke="#000" stroke-width="1"/>`;
    }
  }

  return { svg, midx, midy };
}

function svgSibLines(marriage, midx, midy) {
  const b = marriage.bounds();
  const xw = b.width / 2;
  const sp = marriage.sibset;

  if (!marriage.hasBegun()) return '';
  if (sp.length === 0) return '';

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

  if (count === 0) return '';

  const miny = Math.min(...sp.filter(p => p.location.x !== -100 && p.location.y !== -100 && p.hasBegun()).map(p => p.location.y)) - 5;
  minx += xw;
  maxx += xw;

  let svg = '';

  // Horizontal line across siblings
  svg += `<line x1="${minx}" y1="${miny}" x2="${maxx}" y2="${miny}" stroke="#000" stroke-width="1"/>`;

  // Vertical line from parent link to sibling line
  svg += `<line x1="${marriage.location.x + xw}" y1="${miny}" x2="${marriage.location.x + xw}" y2="${midy}" stroke="#000" stroke-width="1"/>`;

  // Vertical lines down to each sibling
  for (const p of sp) {
    if (p.location.x !== -100 && p.location.y !== -100 && p.hasBegun()) {
      svg += `<line x1="${p.location.x + xw}" y1="${p.location.y}" x2="${p.location.x + xw}" y2="${miny}" stroke="#000" stroke-width="1"/>`;
    }
  }

  return svg;
}

function escapeXml(str) {
  return str.replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&apos;');
}

// Render the kinship diagram as SVG
export function renderSVG(editorState) {
  const { originX, originY, whichFolk, whichKnot } = editorState;

  // Use the same bounding box logic as the canvas renderer
  const bb = resetBoundingBox();

  // If empty diagram, use default size
  let minX = bb.minx;
  let minY = bb.miny;
  let maxX = bb.maxx;
  let maxY = bb.maxy;

  // Check if we have any content
  if (maxX < -20000) {
    // Empty diagram - use default bounds
    minX = 0;
    minY = 0;
    maxX = 800;
    maxY = 600;
  }

  // Add padding
  const padding = 40;
  minX -= padding;
  minY -= padding;
  maxX += padding;
  maxY += padding;

  const width = maxX - minX;
  const height = maxY - minY;

  let svg = `<?xml version="1.0" encoding="UTF-8"?>\n`;
  svg += `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="${minX} ${minY} ${width} ${height}">\n`;
  svg += `  <g transform="translate(${-originX}, ${-originY})">\n`;

  // Draw persons
  for (let i = state.folkIndex; i >= 0; i--) {
    const p = state.folks[i];
    if (!p) continue;
    const color = (i === whichFolk) ? '#ff0000' : '#000000';
    svg += '    ' + svgPersonSymbol(p, color) + '\n';
  }

  // Draw marriages (symbols + lines)
  for (let i = state.knotIndex; i >= 0; i--) {
    const m = state.knots[i];
    if (!m) continue;
    const color = (i === whichKnot) ? '#ff0000' : '#000000';
    svg += '    ' + svgMarriageSymbol(m, color) + '\n';

    const { svg: spouseLines, midx, midy } = svgSpouseLines(m);
    svg += '    ' + spouseLines + '\n';
    svg += '    ' + svgSibLines(m, midx, midy) + '\n';
  }

  svg += '  </g>\n';
  svg += '</svg>';

  return svg;
}
