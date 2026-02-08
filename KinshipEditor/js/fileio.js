// XML file I/O for .kin files
// Port of XFile.java, Person.readXML, Marriage.readXML, KinEditPanel.saveFile/loadFile

import { state, Person, Marriage, SEX_FEMALE, SEX_MALE, SEX_NEUTER, resetState } from './model.js';

// Parse a .kin XML file and populate state
export function loadFromXML(xmlString) {
  const parser = new DOMParser();
  const doc = parser.parseFromString(xmlString, 'text/xml');

  if (doc.querySelector('parsererror')) {
    console.error('XML parse error');
    return null;
  }

  const kindata = doc.querySelector('kindata');
  if (!kindata) {
    console.error('Not a valid KinEditor file');
    return null;
  }

  // Reset state
  resetState();

  // Parse people
  const personEls = kindata.querySelectorAll('people > person');
  const personById = {};

  for (const el of personEls) {
    const name = getTagText(el, 'name');
    const id = parseInt(getTagText(el, 'id'), 10);
    const sex = getTagText(el, 'sex');
    const bornEl = el.querySelector('born');
    const diedEl = el.querySelector('died');
    const born = bornEl ? bornEl.textContent.replace(/#/g, ' ') : 'NA';
    const bornq = bornEl ? (bornEl.getAttribute('q') || '9') : '9';
    const died = diedEl ? diedEl.textContent.replace(/#/g, ' ') : 'NA';
    const diedq = diedEl ? (diedEl.getAttribute('q') || '9') : '9';
    const locx = parseInt(getTagText(el, 'x'), 10);
    const locy = parseInt(getTagText(el, 'y'), 10);
    const comment = getTagText(el, 'comment') || 'No Comment';

    let sexType;
    if (sex.toLowerCase() === 'female') sexType = SEX_FEMALE;
    else if (sex.toLowerCase() === 'male') sexType = SEX_MALE;
    else sexType = SEX_NEUTER;

    const person = new Person(sexType, locx, locy);
    person.name = name;
    person.myId = id;
    person.yob = born;
    person.yobq = bornq;
    person.yod = died;
    person.yodq = diedq;
    person.comment = comment;

    state.idCounter = Math.max(state.idCounter, id);

    // Store in folks array at index (id - 1)
    const idx = id - 1;
    while (state.folkIndex < idx) {
      state.folkIndex++;
      if (!state.folks[state.folkIndex]) state.folks[state.folkIndex] = null;
    }
    state.folks[idx] = person;
    if (idx > state.folkIndex) state.folkIndex = idx;
    personById[id] = person;
  }

  // Parse unions
  const unionEls = kindata.querySelectorAll('unions > union');

  for (const el of unionEls) {
    const id = parseInt(getTagText(el, 'id'), 10);
    const locEl = el.querySelector('location');
    const locx = parseInt(getTagText(locEl, 'x'), 10);
    const locy = parseInt(getTagText(locEl, 'y'), 10);

    // Place marriage at index (id - 1), filling gaps with null
    const targetIdx = id - 1;
    while (state.knotIndex < targetIdx) {
      state.knotIndex++;
      if (!state.knots[state.knotIndex]) state.knots[state.knotIndex] = null;
    }
    if (targetIdx > state.knotIndex) state.knotIndex = targetIdx;

    const marriage = new Marriage(locx, locy);
    marriage.mid = id;
    marriage.id = id;

    const typeEl = el.querySelector('type');
    if (typeEl) marriage.type = typeEl.textContent.replace(/#/g, ' ');

    const beginEl = el.querySelector('begin');
    if (beginEl) {
      marriage.begin = beginEl.textContent.replace(/#/g, ' ');
      marriage.beginq = beginEl.getAttribute('q') || '9';
    }

    const endEl = el.querySelector('end');
    if (endEl) {
      marriage.end = endEl.textContent.replace(/#/g, ' ');
      marriage.endq = endEl.getAttribute('q') || '9';
    }

    const reasonEl = el.querySelector('reason');
    if (reasonEl) marriage.reason = reasonEl.textContent;

    const commentEl = el.querySelector('comment');
    if (commentEl) marriage.comment = commentEl.textContent;

    // Partners
    const partnerEls = el.querySelectorAll('partners > partner');
    for (const pEl of partnerEls) {
      const pid = parseInt(pEl.textContent, 10);
      const person = personById[pid];
      if (person) marriage.addSpouse(person);
    }

    // Siblings
    const siblingEls = el.querySelectorAll('siblings > sibling');
    for (const sEl of siblingEls) {
      const sid = parseInt(sEl.textContent, 10);
      const person = personById[sid];
      if (person) marriage.addSib(person);
    }

    state.knots[targetIdx] = marriage;
  }

  // Parse parameters
  const params = kindata.querySelector('parameters');
  let result = {
    originX: 0, originY: 0,
    whichFolk: -1, whichKnot: -1,
    doLabel: 0,
    beginYear: 'NA', endYear: 'NA',
    editable: true, fixEgo: false,
  };

  if (params) {
    const originEl = params.querySelector('origin');
    if (originEl) {
      result.originX = parseInt(getTagText(originEl, 'x'), 10) || 0;
      result.originY = parseInt(getTagText(originEl, 'y'), 10) || 0;
    }
    const egoEl = params.querySelector('ego');
    if (egoEl) result.whichFolk = parseInt(egoEl.textContent, 10) - 1;
    const marriageEl = params.querySelector('marriage');
    if (marriageEl) result.whichKnot = parseInt(marriageEl.textContent, 10) - 1;
    const labelEl = params.querySelector('label');
    if (labelEl) result.doLabel = parseInt(labelEl.textContent, 10);
    const beginEl = params.querySelector('beginyear');
    if (beginEl) result.beginYear = beginEl.textContent.replace(/#/g, ' ');
    const endEl = params.querySelector('endyear');
    if (endEl) result.endYear = endEl.textContent.replace(/#/g, ' ');
    const editableEl = params.querySelector('editable');
    if (editableEl) result.editable = editableEl.textContent.toLowerCase() === 'true';
    const egofixedEl = params.querySelector('egofixed');
    if (egofixedEl) result.fixEgo = egofixedEl.textContent.toLowerCase() === 'true';
  }

  return result;
}

// Save current state to XML string
export function saveToXML(editorState) {
  const { originX, originY, whichFolk, whichKnot, doLabel, beginYear, endYear, editable, fixEgo } = editorState;
  let xml = '';
  const EOL = '\n';

  xml += '<?xml version="1.0"?>' + EOL;
  xml += '<!DOCTYPE kindata>' + EOL + EOL;
  xml += '<!--  Kinship Editor Save File - Do not edit by hand!  -->' + EOL + EOL;
  xml += '<kindata>' + EOL;

  // People
  xml += '<people>' + EOL;
  for (let i = 0; i <= state.folkIndex; i++) {
    const p = state.folks[i];
    if (!p) continue;
    xml += '  <person>' + EOL;
    xml += `    <name>${escXml(p.name)}</name><id>${p.myId}</id><sex>${p.sex}</sex>` + EOL;
    const yob = (p.yob || 'NA').replace(/ /g, '#');
    const yod = (p.yod || 'NA').replace(/ /g, '#');
    xml += `    <stats><born q="${p.yobq}">${yob}</born><died q="${p.yodq}">${yod}</died></stats>` + EOL;
    xml += `    <location><x>${p.location.x}</x><y>${p.location.y}</y></location>` + EOL;
    xml += `    <comment>${escXml(p.comment || 'None')}</comment>` + EOL;
    xml += '  </person>' + EOL + EOL;
  }
  xml += '</people>' + EOL + EOL;

  // Unions
  xml += '<unions>' + EOL;
  for (let i = 0; i <= state.knotIndex; i++) {
    const m = state.knots[i];
    if (!m) continue;
    xml += '  <union>' + EOL;
    xml += `    <id>${m.mid}</id><location><x>${m.location.x}</x><y>${m.location.y}</y></location>` + EOL;
    const type = (m.type || 'Marriage').replace(/ /g, '#');
    const begin = (m.begin || 'NA').replace(/ /g, '#');
    const end = (m.end || 'NA').replace(/ /g, '#');
    xml += `    <stats><type>${type}</type>` + EOL;
    xml += `        <begin q="${m.beginq}">${begin}</begin><end q="${m.endq}">${end}</end><reason>${m.reason || 'NA'}</reason></stats>` + EOL;

    if (m.spouses.length > 0) {
      xml += '    <partners>' + EOL;
      for (const p of m.spouses) {
        xml += `      <partner>${p.myId}</partner>` + EOL;
      }
      xml += '    </partners>' + EOL;
    }

    if (m.sibset.length > 0) {
      xml += '    <siblings>' + EOL;
      for (const p of m.sibset) {
        xml += `      <sibling>${p.myId}</sibling>` + EOL;
      }
      xml += '    </siblings>' + EOL;
    }

    xml += `    <comment>${escXml(m.comment || 'None')}</comment>` + EOL;
    xml += '  </union>' + EOL;
  }
  xml += '</unions>' + EOL + EOL;

  // Parameters
  xml += '<parameters>' + EOL;
  xml += `  <origin><x>${originX}</x><y>${originY}</y></origin>` + EOL;
  xml += `  <ego>${whichFolk + 1}</ego>` + EOL;
  xml += `  <marriage>${whichKnot + 1}</marriage>` + EOL;
  xml += `  <label>${doLabel}</label>` + EOL;
  xml += `  <beginyear>${beginYear || 'NA'}</beginyear>` + EOL;
  xml += `  <endyear>${endYear || 'NA'}</endyear>` + EOL;
  xml += `  <editable>${editable}</editable>` + EOL;
  xml += `  <egofixed>${fixEgo}</egofixed>` + EOL;
  xml += '</parameters>' + EOL;
  xml += '</kindata>' + EOL;

  return xml;
}

function getTagText(parent, tagName) {
  const el = parent.querySelector(tagName);
  return el ? el.textContent : '';
}

function escXml(s) {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&apos;');
}
