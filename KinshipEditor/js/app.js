// Main application: event handling, UI, orchestration
// Port of KinshipEditor.java + KinEditPanel.java

import { state, Person, Marriage, findPerson, findMarriage, findFreePerson, findFreeMarriage,
         resetState, resetBoundingBox, SEX_FEMALE, SEX_MALE, SEX_NEUTER,
         NOLABEL, INITIALS, FIRST, LAST, WHOLE } from './model.js';
import { render } from './renderer.js';
import { loadFromXML, saveToXML } from './fileio.js';

// Editor state
const editor = {
  originX: 0,
  originY: 0,
  whichFolk: -1,
  whichKnot: -1,
  tiedKnot: -1,
  whichHalf: -1,
  selectLine: null,
  lastLoc: { x: -1, y: -1 },
  lastPersonLoc: null,
  lastFolk: -1,
  lastKnot: -1,
  fixEgo: false,
  editable: true,
  dirty: false,
  loading: false,
  doLabel: WHOLE,
  refYear: 0,
  fileName: '',
  infoPerson: null,
  infoMarriage: null,
  // Timer/animation
  timerInterval: null,
  timerYear: -1,
  timerEndYear: -1,
  timerPaused: false,
};

// DOM elements
const canvas = document.getElementById('kinship-canvas');
const ctx = canvas.getContext('2d');
const container = document.getElementById('canvas-container');
const contextMenu = document.getElementById('context-menu');
const fileInput = document.getElementById('file-input');

// Fields
const fieldName = document.getElementById('field-name');
const fieldBirthYear = document.getElementById('field-birthyear');
const fieldDeathYear = document.getElementById('field-deathyear');
const fieldComment = document.getElementById('field-comment');
const fieldReason = document.getElementById('field-reason');
const fieldBeginYear = document.getElementById('field-beginyear');
const fieldEndYear = document.getElementById('field-endyear');
const labelName = document.getElementById('label-name');
const labelReason = document.getElementById('label-reason');
const chkEditable = document.getElementById('chk-editable');
const chkFixEgo = document.getElementById('chk-fixego');
const btnStep = document.getElementById('btn-step');
const currentYear = document.getElementById('current-year');

// Resize canvas to fill container, growing to fit content
function resizeCanvas() {
  let minW = 2000, minH = 2000;
  const bb = resetBoundingBox();
  if (bb.maxx > -20000) {
    minW = Math.max(minW, bb.maxx + 200);
    minH = Math.max(minH, bb.maxy + 200);
  }
  canvas.width = Math.max(container.clientWidth, minW);
  canvas.height = Math.max(container.clientHeight, minH);
  repaint();
}

// Repaint the canvas
function repaint() {
  state.doLabel = editor.doLabel;
  const result = render(ctx, canvas.width, canvas.height, {
    originX: editor.originX,
    originY: editor.originY,
    whichFolk: editor.whichFolk,
    whichKnot: editor.whichKnot,
    selectLine: editor.selectLine,
  });
  editor.tiedKnot = result.tiedKnot;
  editor.whichHalf = result.whichHalf;

  // Show info for selected person/marriage
  if (editor.lastFolk !== editor.whichFolk && editor.whichFolk >= 0) {
    showPersonInfo(state.folks[editor.whichFolk]);
  }
  if (editor.lastKnot !== editor.whichKnot && editor.whichKnot >= 0) {
    showMarriageInfo(state.knots[editor.whichKnot]);
  }
  if (editor.whichFolk === -1 && editor.whichKnot === -1 && !editor.selectLine) {
    clearInfo();
  }
  editor.lastFolk = editor.whichFolk;
  editor.lastKnot = editor.whichKnot;
}

// Get mouse position relative to canvas with origin offset
function getMousePos(e) {
  const rect = canvas.getBoundingClientRect();
  const scrollX = container.scrollLeft;
  const scrollY = container.scrollTop;
  return {
    x: e.clientX - rect.left + scrollX - editor.originX,
    y: e.clientY - rect.top + scrollY - editor.originY,
  };
}

// Get mouse position for context menu positioning (no origin offset)
function getCanvasPos(e) {
  const rect = canvas.getBoundingClientRect();
  return {
    x: e.clientX - rect.left + container.scrollLeft,
    y: e.clientY - rect.top + container.scrollTop,
  };
}

// Mouse down handler
canvas.addEventListener('mousedown', (e) => {
  const pos = getMousePos(e);
  const which = findPerson(pos.x, pos.y);

  if (which >= 0) {
    hideContextMenu();
    if (editor.whichFolk === which) {
      editor.lastLoc = { ...pos };
    } else {
      if (!editor.fixEgo) editor.whichFolk = which;
    }
    editor.lastPersonLoc = { ...state.folks[which].location };
    if (editor.fixEgo) doFixEgo(which);
    editor.whichKnot = -1;
    if (editor.editable) storeInfo();
    repaint();
    return;
  } else if (!editor.fixEgo) {
    editor.whichFolk = -1;
  }

  const knotIdx = findMarriage(pos.x, pos.y);
  if (knotIdx >= 0) {
    hideContextMenu();
    if (editor.whichKnot === knotIdx) {
      editor.lastLoc = { ...pos };
    } else {
      if (!editor.fixEgo) editor.whichKnot = knotIdx;
    }
    if (!editor.fixEgo) editor.whichFolk = -1;
    if (editor.editable) storeInfo();
    repaint();
    return;
  } else {
    editor.whichKnot = -1;
  }

  clearInfo();

  if (contextMenu.style.display !== 'none') {
    hideContextMenu();
    editor.lastLoc = { x: -1, y: -1 };
  } else if (editor.editable) {
    editor.lastLoc = { ...pos };
    showContextMenu(e);
    storeInfo();
  }
});

// Mouse move/drag handler
canvas.addEventListener('mousemove', (e) => {
  if (e.buttons !== 1) return; // left button not held
  if (!editor.editable || editor.fixEgo) return;

  const pos = getMousePos(e);

  if (e.shiftKey) {
    if (editor.whichKnot !== -1) {
      const m = state.knots[editor.whichKnot];
      const dx = m.location.x - pos.x + 10;
      const dy = m.location.y - pos.y + 10;
      m.deltaMove(dx, dy);
      editor.whichFolk = -1;
      editor.dirty = true;
      repaint();
    } else if (editor.whichFolk !== -1) {
      const p = state.folks[editor.whichFolk];
      editor.selectLine = {
        fromP: { x: p.location.x + 10, y: p.location.y + 10 },
        toP: { x: pos.x, y: pos.y },
      };
      repaint();
    }
  } else if (e.altKey || e.metaKey) {
    if (editor.whichKnot !== -1) {
      const m = state.knots[editor.whichKnot];
      const dx = m.location.x - pos.x + 10;
      const dy = m.location.y - pos.y + 10;
      m.lineageDeltaMove(dx, dy);
      editor.whichFolk = -1;
      editor.dirty = true;
      repaint();
    } else if (editor.whichFolk !== -1) {
      const p = state.folks[editor.whichFolk];
      editor.selectLine = {
        fromP: { x: p.location.x + 10, y: p.location.y + 10 },
        toP: { x: pos.x, y: pos.y },
      };
      repaint();
    }
  } else {
    editor.selectLine = null;
    if (editor.whichFolk !== -1) {
      const p = state.folks[editor.whichFolk];
      p.location.x = pos.x - 10;
      p.location.y = pos.y - 10;
      editor.whichKnot = -1;
      editor.dirty = true;
      repaint();
    } else if (editor.whichKnot !== -1) {
      const m = state.knots[editor.whichKnot];
      m.location.x = pos.x - 10;
      m.location.y = pos.y - 10;
      editor.dirty = true;
      repaint();
    }
  }
});

// Mouse up handler
canvas.addEventListener('mouseup', (e) => {
  if (!editor.editable) return;

  const pos = getMousePos(e);

  if (e.ctrlKey && !e.shiftKey && !e.metaKey && !e.altKey) {
    // Delete on ctrl+click
    const which = findPerson(pos.x, pos.y);
    if (which >= 0) {
      state.folks[which].delPerson();
      state.folks[which] = null;
      editor.whichFolk = -1;
      editor.dirty = true;
      repaint();
      return;
    }

    const knotIdx = findMarriage(pos.x, pos.y);
    if (knotIdx >= 0) {
      state.knots[knotIdx].delMarriage();
      state.knots[knotIdx] = null;
      editor.whichKnot = -1;
      editor.dirty = true;
      repaint();
      return;
    }
  } else {
    // Relationship creation/toggle (shift-drag or meta-drag to union)
    if (editor.selectLine && editor.whichHalf > 0 && editor.whichFolk > -1 && editor.tiedKnot >= 0) {
      const mx = state.knots[editor.tiedKnot];
      const px = state.folks[editor.whichFolk];

      if (editor.whichHalf === 1) {
        // Top half = spouse
        if (!mx.isSpouse(px)) {
          if (mx.isSib(px)) {
            mx.delSib(px);
            if (editor.lastPersonLoc && editor.lastPersonLoc.y > mx.location.y) {
              editor.lastPersonLoc.y = mx.location.y + (mx.location.y - editor.lastPersonLoc.y);
            }
          }
          mx.addSpouse(px);
          if (editor.lastPersonLoc) px.setLocation(editor.lastPersonLoc.x, editor.lastPersonLoc.y);
          editor.dirty = true;
        } else {
          mx.delSpouse(px);
          if (editor.lastPersonLoc) px.setLocation(editor.lastPersonLoc.x, editor.lastPersonLoc.y);
          editor.dirty = true;
        }
      } else if (editor.whichHalf === 2) {
        // Bottom half = sibling/child
        if (!mx.isSib(px)) {
          if (mx.isSpouse(px)) {
            mx.delSpouse(px);
            if (editor.lastPersonLoc && editor.lastPersonLoc.y < mx.location.y) {
              editor.lastPersonLoc.y = mx.location.y + (mx.location.y - editor.lastPersonLoc.y);
            }
          }
          mx.addSib(px);
          if (editor.lastPersonLoc) px.setLocation(editor.lastPersonLoc.x, editor.lastPersonLoc.y);
          editor.dirty = true;
        } else {
          mx.delSib(px);
          if (editor.lastPersonLoc) px.setLocation(editor.lastPersonLoc.x, editor.lastPersonLoc.y);
          editor.dirty = true;
        }
      }
    }

    editor.whichHalf = -1;
    editor.tiedKnot = -1;
    editor.selectLine = null;
    repaint();
  }
});

// Context menu
function showContextMenu(e) {
  const canvasPos = getCanvasPos(e);
  contextMenu.style.display = 'block';
  contextMenu.style.left = canvasPos.x + 'px';
  contextMenu.style.top = canvasPos.y + 'px';
}

function hideContextMenu() {
  contextMenu.style.display = 'none';
}

// Context menu item click
contextMenu.addEventListener('click', (e) => {
  const item = e.target.closest('.context-item');
  if (!item) return;

  const type = item.dataset.type;
  hideContextMenu();

  let i;
  switch (type) {
    case 'Female':
      i = findFreePerson();
      state.folks[i] = new Person(SEX_FEMALE, editor.lastLoc.x, editor.lastLoc.y);
      state.folks[i].myId = i + 1;
      state.idCounter = Math.max(state.idCounter, i + 1);
      break;
    case 'Male':
      i = findFreePerson();
      state.folks[i] = new Person(SEX_MALE, editor.lastLoc.x, editor.lastLoc.y);
      state.folks[i].myId = i + 1;
      state.idCounter = Math.max(state.idCounter, i + 1);
      break;
    case 'Neuter':
      i = findFreePerson();
      state.folks[i] = new Person(SEX_NEUTER, editor.lastLoc.x, editor.lastLoc.y);
      state.folks[i].myId = i + 1;
      state.idCounter = Math.max(state.idCounter, i + 1);
      break;
    case 'Union':
      i = findFreeMarriage();
      state.knots[i] = new Marriage(editor.lastLoc.x, editor.lastLoc.y);
      state.knots[i].mid = i + 1;
      break;
  }

  editor.dirty = true;
  repaint();
});

// Close context menu when clicking outside
document.addEventListener('mousedown', (e) => {
  if (!contextMenu.contains(e.target) && contextMenu.style.display !== 'none') {
    hideContextMenu();
  }
});

// Property panel info display
function showPersonInfo(person) {
  editor.infoPerson = person;
  editor.infoMarriage = null;
  labelName.style.display = '';
  fieldName.style.display = '';
  labelReason.style.display = 'none';
  fieldReason.style.display = 'none';
  setBirthDeathLabels('Birth Year', 'Death Year');
  fieldName.value = person.name;
  fieldBirthYear.value = person.yob;
  fieldDeathYear.value = person.yod;
  fieldComment.value = person.comment;
}

function showMarriageInfo(marriage) {
  editor.infoPerson = null;
  editor.infoMarriage = marriage;
  labelName.style.display = 'none';
  fieldName.style.display = 'none';
  labelReason.style.display = '';
  fieldReason.style.display = '';
  setBirthDeathLabels('Begin Year', 'End Year');
  fieldReason.value = marriage.reason;
  fieldBirthYear.value = marriage.begin;
  fieldDeathYear.value = marriage.end;
  fieldComment.value = marriage.comment;
}

function clearInfo() {
  if (editor.infoPerson || editor.infoMarriage) {
    storeInfo();
  }
  editor.infoPerson = null;
  editor.infoMarriage = null;
  fieldName.value = '';
  fieldBirthYear.value = '';
  fieldDeathYear.value = '';
  fieldComment.value = '';
  fieldReason.value = 'NA';
}

function storeInfo() {
  if (editor.infoPerson) {
    const p = editor.infoPerson;
    if (p.name !== fieldName.value) { p.name = fieldName.value; editor.dirty = true; }
    if (p.yob !== fieldBirthYear.value) { p.yob = fieldBirthYear.value; editor.dirty = true; }
    if (p.yod !== fieldDeathYear.value) { p.yod = fieldDeathYear.value; editor.dirty = true; }
    if (p.comment !== fieldComment.value) { p.comment = fieldComment.value; editor.dirty = true; }
  } else if (editor.infoMarriage) {
    const m = editor.infoMarriage;
    if (m.reason !== fieldReason.value) { m.reason = fieldReason.value; editor.dirty = true; }
    if (m.begin !== fieldBirthYear.value) { m.begin = fieldBirthYear.value; editor.dirty = true; }
    if (m.end !== fieldDeathYear.value) { m.end = fieldDeathYear.value; editor.dirty = true; }
    if (m.comment !== fieldComment.value) { m.comment = fieldComment.value; editor.dirty = true; }
  }
}

function setBirthDeathLabels(birthText, deathText) {
  const labels = document.querySelectorAll('#panel-top .panel-row:first-child label');
  if (labels[0]) labels[0].textContent = birthText;
  if (labels[1]) labels[1].textContent = deathText;
}

// Field change listeners
fieldName.addEventListener('change', () => {
  if (editor.infoPerson) {
    editor.infoPerson.name = fieldName.value;
    editor.dirty = true;
    if (state.doLabel !== NOLABEL) repaint();
  }
});

fieldBirthYear.addEventListener('change', () => {
  if (editor.infoPerson) { editor.infoPerson.yob = fieldBirthYear.value; editor.dirty = true; }
  else if (editor.infoMarriage) { editor.infoMarriage.begin = fieldBirthYear.value; editor.dirty = true; }
  repaint();
});

fieldDeathYear.addEventListener('change', () => {
  if (editor.infoPerson) { editor.infoPerson.yod = fieldDeathYear.value; editor.dirty = true; }
  else if (editor.infoMarriage) { editor.infoMarriage.end = fieldDeathYear.value; editor.dirty = true; }
  repaint();
});

fieldComment.addEventListener('change', () => {
  if (editor.infoPerson) { editor.infoPerson.comment = fieldComment.value; editor.dirty = true; }
  else if (editor.infoMarriage) { editor.infoMarriage.comment = fieldComment.value; editor.dirty = true; }
});

fieldReason.addEventListener('change', () => {
  if (editor.infoMarriage) { editor.infoMarriage.reason = fieldReason.value; editor.dirty = true; }
});

// Editable / Fix Ego checkboxes
chkEditable.addEventListener('change', () => {
  editor.editable = chkEditable.checked;
  setFieldsEditable(editor.editable);
  editor.dirty = true;
});

chkFixEgo.addEventListener('change', () => {
  editor.fixEgo = chkFixEgo.checked;
  editor.dirty = true;
});

function setFieldsEditable(e) {
  fieldName.disabled = !e;
  fieldBirthYear.disabled = !e;
  fieldDeathYear.disabled = !e;
  fieldComment.disabled = !e;
  fieldBeginYear.disabled = !e;
  fieldEndYear.disabled = !e;
  chkFixEgo.parentElement.style.display = e ? '' : 'none';
}

function doFixEgo(which) {
  if (editor.whichFolk === -1) {
    editor.whichFolk = which;
  }
}

// Menu system
const menuItems = document.querySelectorAll('.menu-item');
let openMenu = null;

menuItems.forEach(item => {
  item.addEventListener('click', (e) => {
    if (openMenu === item) {
      item.classList.remove('open');
      openMenu = null;
    } else {
      if (openMenu) openMenu.classList.remove('open');
      item.classList.add('open');
      openMenu = item;
    }
    e.stopPropagation();
  });
});

document.addEventListener('click', () => {
  if (openMenu) {
    openMenu.classList.remove('open');
    openMenu = null;
  }
});

// Menu actions
document.getElementById('action-new').addEventListener('click', doNew);
document.getElementById('action-open').addEventListener('click', doOpen);
document.getElementById('action-save').addEventListener('click', doSave);
document.getElementById('action-saveas').addEventListener('click', doSaveAs);
document.getElementById('action-clearall').addEventListener('click', doClearAll);
document.getElementById('action-render').addEventListener('click', () => doRender(true));
document.getElementById('action-renderchart').addEventListener('click', () => doRender(false));

// Label menu items
document.querySelectorAll('.menu-check').forEach(item => {
  item.addEventListener('click', (e) => {
    document.querySelectorAll('.menu-check').forEach(x => x.classList.remove('active'));
    item.classList.add('active');
    editor.doLabel = parseInt(item.dataset.label);
    state.doLabel = editor.doLabel;
    editor.dirty = true;
    repaint();
    e.stopPropagation();
  });
});

// File operations
function doNew() {
  if (editor.dirty) {
    showSaveDialog(() => {
      stopTimer();
      deleteAll();
      editor.fixEgo = false;
      chkFixEgo.checked = false;
    });
  } else {
    stopTimer();
    deleteAll();
    editor.fixEgo = false;
    chkFixEgo.checked = false;
  }
}

function doOpen() {
  if (editor.dirty) {
    showSaveDialog(() => fileInput.click());
  } else {
    fileInput.click();
  }
}

fileInput.addEventListener('change', (e) => {
  const file = e.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = (ev) => {
    loadFileContent(ev.target.result, file.name);
  };
  reader.readAsText(file);
  fileInput.value = '';
});

function loadFileContent(xmlString, fileName) {
  editor.loading = true;
  deleteAll();

  const params = loadFromXML(xmlString);
  if (!params) {
    alert('Failed to load file. Not a valid KinEditor file.');
    editor.loading = false;
    return;
  }

  editor.originX = params.originX;
  editor.originY = params.originY;
  editor.whichFolk = params.whichFolk;
  editor.whichKnot = params.whichKnot;
  editor.doLabel = params.doLabel;
  editor.editable = params.editable;
  editor.fixEgo = params.fixEgo;
  editor.fileName = fileName || '';
  editor.dirty = false;
  editor.lastFolk = -1;
  editor.lastKnot = -1;

  // Update UI
  state.doLabel = editor.doLabel;
  chkEditable.checked = editor.editable;
  chkFixEgo.checked = editor.fixEgo;
  setFieldsEditable(editor.editable);
  fieldBeginYear.value = params.beginYear;
  fieldEndYear.value = params.endYear;

  // Update label menu checkmark
  document.querySelectorAll('.menu-check').forEach(x => x.classList.remove('active'));
  const labelItem = document.querySelector(`.menu-check[data-label="${editor.doLabel}"]`);
  if (labelItem) labelItem.classList.add('active');

  // Set ref year
  setRefYear(params.beginYear);

  // Set scroll
  container.scrollLeft = -editor.originX;
  container.scrollTop = -editor.originY;

  editor.loading = false;
  repaint();
}

function doSave() {
  storeInfo();
  const xml = saveToXML({
    originX: editor.originX,
    originY: editor.originY,
    whichFolk: editor.whichFolk,
    whichKnot: editor.whichKnot,
    doLabel: editor.doLabel,
    beginYear: fieldBeginYear.value,
    endYear: fieldEndYear.value,
    editable: editor.editable,
    fixEgo: editor.fixEgo,
  });

  const blob = new Blob([xml], { type: 'application/xml' });
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = editor.fileName || 'kinship.kin';
  a.click();
  URL.revokeObjectURL(a.href);
  editor.dirty = false;
}

function doSaveAs() {
  const name = prompt('File name:', editor.fileName || 'kinship.kin');
  if (name) {
    editor.fileName = name;
    doSave();
  }
}

function doClearAll() {
  if (editor.dirty) {
    showSaveDialog(() => {
      stopTimer();
      deleteAll();
      editor.fixEgo = false;
      chkFixEgo.checked = false;
    });
  } else {
    stopTimer();
    deleteAll();
    editor.fixEgo = false;
    chkFixEgo.checked = false;
  }
}

function deleteAll() {
  editor.whichFolk = -1;
  editor.whichKnot = -1;
  editor.lastFolk = -1;
  editor.lastKnot = -1;
  resetState();
  editor.dirty = false;
  clearInfo();
  editor.originX = 0;
  editor.originY = 0;
  container.scrollLeft = 0;
  container.scrollTop = 0;
  repaint();
}

// Render to PNG (replaces JPEG export)
function doRender(doBounds) {
  storeInfo();
  let renderCanvas, renderCtx, translateX, translateY;

  if (doBounds) {
    renderCanvas = document.createElement('canvas');
    renderCanvas.width = container.clientWidth;
    renderCanvas.height = container.clientHeight;
    renderCtx = renderCanvas.getContext('2d');
    translateX = editor.originX;
    translateY = editor.originY;
  } else {
    const bb = resetBoundingBox();
    renderCanvas = document.createElement('canvas');
    renderCanvas.width = bb.maxx - bb.minx + 80;
    renderCanvas.height = bb.maxy - bb.miny + 80;
    renderCtx = renderCanvas.getContext('2d');
    translateX = -bb.minx + 20;
    translateY = -bb.miny + 20;
  }

  render(renderCtx, renderCanvas.width, renderCanvas.height, {
    originX: translateX,
    originY: translateY,
    whichFolk: editor.whichFolk,
    whichKnot: editor.whichKnot,
    selectLine: null,
  });

  renderCanvas.toBlob((blob) => {
    const a = document.createElement('a');
    let fname = editor.fileName || 'kinship';
    if (fname.includes('.')) fname = fname.substring(0, fname.lastIndexOf('.'));
    if (editor.whichFolk >= 0) fname += '_' + state.folks[editor.whichFolk].name;
    else fname += '_NoEgo';
    if (editor.refYear) fname += '_' + editor.refYear;
    a.href = URL.createObjectURL(blob);
    a.download = fname + '.png';
    a.click();
    URL.revokeObjectURL(a.href);
  }, 'image/png');
}

// Save dialog
function showSaveDialog(callback) {
  const dialog = document.getElementById('save-dialog');
  dialog.style.display = 'flex';

  const onSave = () => {
    cleanup();
    doSave();
    callback();
  };
  const onDontSave = () => {
    cleanup();
    editor.dirty = false;
    callback();
  };
  const onCancel = () => {
    cleanup();
  };

  function cleanup() {
    dialog.style.display = 'none';
    document.getElementById('save-yes').removeEventListener('click', onSave);
    document.getElementById('save-no').removeEventListener('click', onDontSave);
    document.getElementById('save-cancel').removeEventListener('click', onCancel);
  }

  document.getElementById('save-yes').addEventListener('click', onSave);
  document.getElementById('save-no').addEventListener('click', onDontSave);
  document.getElementById('save-cancel').addEventListener('click', onCancel);
}

// Help floating panel
const helpPanel = document.getElementById('help-panel');
const helpTitlebar = document.getElementById('help-titlebar');
const helpContent = document.getElementById('help-content');
let helpTextLoaded = false;

document.getElementById('action-help').addEventListener('click', () => {
  if (helpPanel.style.display === 'none' || helpPanel.style.display === '') {
    helpPanel.style.display = 'flex';
    // Position to the right of the window on first open
    if (!helpPanel.dataset.positioned) {
      helpPanel.style.top = '40px';
      helpPanel.style.right = '20px';
      helpPanel.style.left = 'auto';
      helpPanel.dataset.positioned = '1';
    }
    if (!helpTextLoaded) {
      fetch('resources/help.txt')
        .then(r => r.ok ? r.text() : Promise.reject('Could not load help.txt'))
        .then(text => {
          helpContent.textContent = text;
          helpTextLoaded = true;
        })
        .catch(() => {
          helpContent.textContent = 'Help file could not be loaded.';
        });
    }
  } else {
    helpPanel.style.display = 'none';
  }
});

document.getElementById('help-close').addEventListener('click', () => {
  helpPanel.style.display = 'none';
});

// Draggable help panel
(function () {
  let dragging = false;
  let dragOffsetX = 0, dragOffsetY = 0;

  helpTitlebar.addEventListener('mousedown', (e) => {
    dragging = true;
    const rect = helpPanel.getBoundingClientRect();
    dragOffsetX = e.clientX - rect.left;
    dragOffsetY = e.clientY - rect.top;
    e.preventDefault();
  });

  document.addEventListener('mousemove', (e) => {
    if (!dragging) return;
    // Switch from right-anchored to left-anchored positioning on first drag
    helpPanel.style.right = 'auto';
    helpPanel.style.left = (e.clientX - dragOffsetX) + 'px';
    helpPanel.style.top = (e.clientY - dragOffsetY) + 'px';
  });

  document.addEventListener('mouseup', () => {
    dragging = false;
  });
})();

// Timeline / Animation
function setRefYear(yearStr) {
  let yr = parseInt(yearStr, 10);
  if (isNaN(yr)) yr = 0;
  editor.refYear = yr;
  state.refYear = yr > 0 ? String(yr) : '';
}

fieldBeginYear.addEventListener('change', () => {
  setRefYear(fieldBeginYear.value);
  repaint();
});

fieldEndYear.addEventListener('change', () => {
  const val = fieldEndYear.value.trim();
  if (val !== 'NA' && isNaN(parseInt(val, 10))) fieldEndYear.value = 'NA';
});

btnStep.addEventListener('click', () => {
  if (editor.timerInterval) {
    if (editor.timerPaused) {
      editor.timerPaused = false;
      resumeTimer();
    } else {
      editor.timerPaused = true;
      pauseTimer();
    }
    return;
  }

  let s, t;
  s = parseInt(fieldBeginYear.value, 10);
  t = parseInt(fieldEndYear.value, 10);
  if (isNaN(s) || isNaN(t)) return;
  if (t < s) { [s, t] = [t, s]; }

  startTimer(s, t);
});

function startTimer(from, to) {
  editor.timerYear = from;
  editor.timerEndYear = to;
  editor.timerPaused = false;
  currentYear.style.display = '';

  editor.timerInterval = setInterval(() => {
    if (editor.timerPaused) return;
    setRefYear(String(editor.timerYear));
    currentYear.textContent = editor.timerYear;
    repaint();
    editor.timerYear++;
    if (editor.timerYear > editor.timerEndYear) {
      stopTimer();
    }
  }, 1600);
}

function pauseTimer() {
  // Interval keeps running but timerPaused flag prevents action
}

function resumeTimer() {
  editor.timerPaused = false;
}

function stopTimer() {
  if (editor.timerInterval) {
    clearInterval(editor.timerInterval);
    editor.timerInterval = null;
  }
  editor.timerYear = -1;
  editor.timerPaused = false;
  currentYear.textContent = '';
  currentYear.style.display = 'none';
  setRefYear('0');
}

// Scroll handling
container.addEventListener('scroll', () => {
  editor.originX = -container.scrollLeft;
  editor.originY = -container.scrollTop;
  repaint();
});

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
  if (e.ctrlKey || e.metaKey) {
    switch (e.key.toLowerCase()) {
      case 'n': e.preventDefault(); doNew(); break;
      case 'o': e.preventDefault(); doOpen(); break;
      case 's':
        e.preventDefault();
        if (e.shiftKey) doSaveAs();
        else doSave();
        break;
      case 'r':
        e.preventDefault();
        doRender(e.shiftKey ? false : true);
        break;
    }
  }
});

// Window resize
window.addEventListener('resize', resizeCanvas);

// Initialize
resizeCanvas();
