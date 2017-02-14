SystemovichWordCountView = require './systemovich-word-count-view'
{CompositeDisposable} = require 'atom'

module.exports = SystemovichWordCount =
  systemovichWordCountView: null
  modalPanel: null
  subscriptions: null

  activate: (state) ->
    @systemovichWordCountView = new SystemovichWordCountView(state.systemovichWordCountViewState)
    @modalPanel = atom.workspace.addModalPanel(item: @systemovichWordCountView.getElement(), visible: false)

    # Events subscribed to in atom's system can be easily cleaned up with a CompositeDisposable
    @subscriptions = new CompositeDisposable

    # Register command that toggles this view
    @subscriptions.add atom.commands.add 'atom-workspace', 'systemovich-word-count:toggle': => @toggle()

  deactivate: ->
    @modalPanel.destroy()
    @subscriptions.dispose()
    @systemovichWordCountView.destroy()

  serialize: ->
    systemovichWordCountViewState: @systemovichWordCountView.serialize()

  toggle: ->
    console.log 'SystemovichWordCount was toggled!'

    if @modalPanel.isVisible()
      @modalPanel.hide()
    else
      editor = atom.workspace.getActiveTextEditor()
      words = editor.getText().split(/\s+/).length
      @systemovichWordCountView.setCount(words)
      @modalPanel.show()
