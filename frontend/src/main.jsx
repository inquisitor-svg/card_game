import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  BarChart3,
  BookOpen,
  CheckCircle2,
  Clock3,
  Headphones,
  Mic,
  Play,
  Send,
  Sparkles,
  Volume2
} from 'lucide-react';
import { evaluatePlacement, evaluateShadowing, fetchModules, fetchQuestions, recordStudySession } from './services/api';
import './styles.css';

const focusOptions = ['听力', '口语', '阅读', '词汇', '语法'];

function App() {
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [placementResult, setPlacementResult] = useState(null);
  const [studyMinutes, setStudyMinutes] = useState(25);
  const [focusAreas, setFocusAreas] = useState(['听力']);
  const [studyResult, setStudyResult] = useState(null);
  const [modules, setModules] = useState([]);
  const [activeModuleId, setActiveModuleId] = useState('');
  const [transcript, setTranscript] = useState('');
  const [shadowResult, setShadowResult] = useState(null);
  const [apiStatus, setApiStatus] = useState('正在连接后端...');

  useEffect(() => {
    Promise.all([fetchQuestions(), fetchModules()])
      .then(([questionData, moduleData]) => {
        setQuestions(questionData);
        setModules(moduleData);
        setActiveModuleId(moduleData[0]?.id || '');
        setApiStatus('后端已连接');
      })
      .catch(() => {
        setApiStatus('后端未启动，页面正在使用演示数据');
        setQuestions(demoQuestions);
        setModules(demoModules);
        setActiveModuleId(demoModules[0].id);
      });
  }, []);

  const activeModule = useMemo(
    () => modules.find((module) => module.id === activeModuleId) || modules[0],
    [modules, activeModuleId]
  );

  const submitPlacement = async () => {
    const payload = {
      answers: questions.map((question) => ({
        questionId: question.id,
        answer: answers[question.id] || ''
      }))
    };
    try {
      setPlacementResult(await evaluatePlacement(payload));
    } catch {
      setPlacementResult(localPlacementScore(questions, answers));
    }
  };

  const submitStudyTime = async () => {
    try {
      setStudyResult(await recordStudySession({ minutes: Number(studyMinutes), focusAreas }));
    } catch {
      setStudyResult({
        minutesToday: Number(studyMinutes),
        weeklyMinutes: Number(studyMinutes) + 90,
        recommendation: '演示建议：今天保持 25-35 分钟即可，末尾留 5 分钟复盘。',
        nextBestAction: '做一段影子跟读，然后写下 3 个可复用句子。'
      });
    }
  };

  const speak = (text) => {
    if (!window.speechSynthesis) return;
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = 'de-DE';
    utterance.rate = 0.86;
    window.speechSynthesis.cancel();
    window.speechSynthesis.speak(utterance);
  };

  const evaluateCurrentShadowing = async () => {
    if (!activeModule) return;
    try {
      setShadowResult(await evaluateShadowing({ targetText: activeModule.shadowText, transcript }));
    } catch {
      setShadowResult({
        pronunciationScore: Math.min(100, Math.round((transcript.length / Math.max(activeModule.shadowText.length, 1)) * 82)),
        rhythmScore: 72,
        feedback: '演示评分：后端未连接时只按文本长度粗略估计。连接语音 API 后可替换为真实发音评分。'
      });
    }
  };

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">DS</div>
          <div>
            <strong>Deutsch Studio</strong>
            <span>German learning lab</span>
          </div>
        </div>
        <nav>
          <a href="#placement"><Sparkles size={18} /> 入门测试</a>
          <a href="#time"><Clock3 size={18} /> 学习时长</a>
          <a href="#training"><Headphones size={18} /> 听说读训练</a>
          <a href="#shadow"><Mic size={18} /> 影子跟读</a>
        </nav>
        <div className="status-pill">{apiStatus}</div>
      </aside>

      <section className="workspace">
        <header className="hero">
          <div>
            <p className="eyebrow">德语学习网站</p>
            <h1>从水平诊断到影子跟读的一站式练习台</h1>
            <p>用宽松语义评分判断入门水平，记录学习时长，并把听、说、读练习串成每天可执行的训练流。</p>
          </div>
          <div className="hero-meter">
            <BarChart3 size={28} />
            <strong>{placementResult?.estimatedLevel || 'A1?'}</strong>
            <span>当前估计水平</span>
          </div>
        </header>

        <section className="grid two-columns">
          <Panel id="placement" icon={<Sparkles />} title="入门测试" action={<button onClick={submitPlacement}><Send size={16} /> 提交评分</button>}>
            <div className="question-list">
              {questions.map((question, index) => (
                <label className="question-item" key={question.id}>
                  <span>{index + 1}. {directionLabel(question.direction)} · {question.level}</span>
                  <strong>{question.prompt}</strong>
                  <textarea
                    value={answers[question.id] || ''}
                    onChange={(event) => setAnswers({ ...answers, [question.id]: event.target.value })}
                    placeholder="输入你的翻译，意思对即可"
                  />
                </label>
              ))}
            </div>
            {placementResult && (
              <ResultBlock
                title={`${placementResult.estimatedLevel} · ${placementResult.overallScore} 分`}
                text={placementResult.recommendation}
              />
            )}
          </Panel>

          <Panel id="time" icon={<Clock3 />} title="学习时长" action={<button onClick={submitStudyTime}><CheckCircle2 size={16} /> 记录</button>}>
            <div className="time-control">
              <label>
                今日学习分钟
                <input type="number" min="0" value={studyMinutes} onChange={(event) => setStudyMinutes(event.target.value)} />
              </label>
              <div className="focus-row">
                {focusOptions.map((option) => (
                  <button
                    className={focusAreas.includes(option) ? 'chip active' : 'chip'}
                    key={option}
                    onClick={() => setFocusAreas(toggle(focusAreas, option))}
                  >
                    {option}
                  </button>
                ))}
              </div>
            </div>
            {studyResult && (
              <div className="stats-row">
                <MiniStat label="今日" value={`${studyResult.minutesToday}m`} />
                <MiniStat label="本周" value={`${studyResult.weeklyMinutes}m`} />
                <ResultBlock title="建议" text={`${studyResult.recommendation} ${studyResult.nextBestAction}`} />
              </div>
            )}
          </Panel>
        </section>

        <section className="grid training-grid">
          <Panel id="training" icon={<BookOpen />} title="听说读训练">
            <div className="module-tabs">
              {modules.map((module) => (
                <button
                  className={module.id === activeModuleId ? 'module-tab active' : 'module-tab'}
                  key={module.id}
                  onClick={() => {
                    setActiveModuleId(module.id);
                    setShadowResult(null);
                    setTranscript('');
                  }}
                >
                  {typeIcon(module.type)}
                  <span>{module.title}</span>
                </button>
              ))}
            </div>
            {activeModule && (
              <article className="training-card">
                <div>
                  <span className="level-badge">{activeModule.level}</span>
                  <h2>{activeModule.title}</h2>
                  <p>{activeModule.content}</p>
                  <small>{activeModule.tip}</small>
                </div>
                <button className="icon-button" onClick={() => speak(activeModule.content)} title="播放德语文本">
                  <Volume2 />
                </button>
              </article>
            )}
          </Panel>

          <Panel id="shadow" icon={<Mic />} title="影子跟读" action={<button onClick={evaluateCurrentShadowing}><Play size={16} /> 评分</button>}>
            {activeModule && (
              <>
                <div className="shadow-target">
                  <span>跟读句子</span>
                  <p>{activeModule.shadowText}</p>
                  <button className="ghost-button" onClick={() => speak(activeModule.shadowText)}><Volume2 size={16} /> 慢速播放</button>
                </div>
                <textarea
                  className="shadow-input"
                  value={transcript}
                  onChange={(event) => setTranscript(event.target.value)}
                  placeholder="先听，再跟读；这里可以粘贴浏览器语音识别转写或外部 API 返回的 transcript"
                />
                {shadowResult && (
                  <div className="score-pair">
                    <MiniStat label="发音接近度" value={`${shadowResult.pronunciationScore}%`} />
                    <MiniStat label="节奏" value={`${shadowResult.rhythmScore}%`} />
                    <ResultBlock title="反馈" text={shadowResult.feedback} />
                  </div>
                )}
              </>
            )}
          </Panel>
        </section>
      </section>
    </main>
  );
}

function Panel({ id, icon, title, action, children }) {
  return (
    <section className="panel" id={id}>
      <div className="panel-header">
        <div className="panel-title">{React.cloneElement(icon, { size: 20 })}<h2>{title}</h2></div>
        {action}
      </div>
      {children}
    </section>
  );
}

function MiniStat({ label, value }) {
  return (
    <div className="mini-stat">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function ResultBlock({ title, text }) {
  return (
    <div className="result-block">
      <strong>{title}</strong>
      <p>{text}</p>
    </div>
  );
}

function directionLabel(direction) {
  return direction === 'ZH_TO_DE' ? '汉译德' : '德译汉';
}

function typeIcon(type) {
  if (type === 'listening') return <Headphones size={16} />;
  if (type === 'speaking') return <Mic size={16} />;
  return <BookOpen size={16} />;
}

function toggle(items, item) {
  return items.includes(item) ? items.filter((current) => current !== item) : [...items, item];
}

function localPlacementScore(questions, answers) {
  const filled = questions.filter((question) => (answers[question.id] || '').trim().length > 0).length;
  const score = Math.round((filled / Math.max(questions.length, 1)) * 72);
  return {
    overallScore: score,
    estimatedLevel: score > 65 ? 'A2' : score > 35 ? 'A1' : 'A0',
    recommendation: '演示评分：启动后端后可使用宽松语义评分。',
    results: []
  };
}

const demoQuestions = [
  { id: 'q1', direction: 'DE_TO_ZH', prompt: 'Ich hätte gern einen Kaffee.', level: 'A1' },
  { id: 'q2', direction: 'ZH_TO_DE', prompt: '我今天没有时间。', level: 'A1' },
  { id: 'q3', direction: 'DE_TO_ZH', prompt: 'Der Zug kommt wegen des Wetters später an.', level: 'A2' }
];

const demoModules = [
  {
    id: 'listen-a1',
    type: 'listening',
    title: '听力：咖啡馆点单',
    level: 'A1',
    content: 'Guten Morgen. Ich hätte gern einen Kaffee und ein Stück Kuchen.',
    shadowText: 'Ich hätte gern einen Kaffee und ein Stück Kuchen.',
    tip: '先听完整句，再跟读重音。'
  }
];

createRoot(document.getElementById('root')).render(<App />);
