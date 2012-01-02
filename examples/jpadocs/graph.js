//label placement on edges 
$jit.ForceDirected.Plot.EdgeTypes.implement({ 
 'fooType': {
 'render': function(adj, canvas) { 
  //plot arrow edge 
  this.edgeTypes.arrow.render.call(this, adj, canvas); 
  //get nodes cartesian coordinates 
  var pos = adj.nodeFrom.pos.getc(true); 
  var posChild = adj.nodeTo.pos.getc(true); 
  //check for edge label in data 
  var data = adj.data; 
  if(data.$labeltext) { 
   //now adjust the label placement 
   var radius = this.viz.canvas.getSize(); 
   var x = parseInt((pos.x + posChild.x - (data.$labeltext.length * 5)) / 2); 
   var y = parseInt((pos.y + posChild.y ) /2); 
   this.viz.canvas.getCtx().fillText(data.$labeltext, x, y); 
  } 
 } 
}
}); 

var Log = {
write: function(text){
 if(console && console.log)
  console.log(text);
 }
};
var fd = new $jit.ForceDirected({  
  //id of the visualization container  
  injectInto: 'graph',  
  //Enable zooming and panning  
  //by scrolling and DnD  
  Navigation: {  
    enable: false
  },  
  // Change node and edge styles such as  
  // color and width.  
  // These properties are also set per node  
  // with dollar prefixed data-properties in the  
  // JSON structure.  
  Node: {  
    overridable: true,  
    color: 'gray',
    type: 'rectangle',
    autoWidth: true,
    autoHeight: true,
    height: 30,
    width: 50,
    padding: 20
  },  
  Edge: {  
    overridable: true,  
    color: '#23A4FF',  
    lineWidth: 0.4  
  },  
  //Native canvas text styling  
  Label: {
    type: 'Native', //Native or HTML  
    size: 12,  
    style: 'bold',
    textBaseline: 'middle'
  },  
  //Add Tips  
  Tips: {  
    enable: true,  
    onShow: function(tip, node) {  
      //count connections  
      var count = 0;  
      node.eachAdjacency(function() { count++; });  
      //display node info in tooltip  
      tip.innerHTML = "<div class=\"tooltip\">Click to view " + node.name + " documentation</div>";  
    }  
  },  
  // Add node events  
  Events: {  
    enable: true,  
    type: 'Native',  
    //Change cursor style when hovering a node  
    onMouseEnter: function() {  
      fd.canvas.getElement().style.cursor = 'pointer';  
    },  
    onMouseLeave: function() {  
      fd.canvas.getElement().style.cursor = '';  
    },  
    //Add also a click handler to nodes  
    onClick: function(node) {  
      if(!node) return;
      var url = node.getData("url");
      document.location = url;
    }  
  },
  Margin: {  
	  top: 50,  
	  left: 100,  
	  right: 100,  
	  bottom: 50  
  },
  //Number of iterations for the FD algorithm  
  iterations: 200,  
  //Edge length  
  levelDistance: 150,
});  
// load JSON data.  
fd.loadJSON(json);  
// compute positions incrementally and animate.  
fd.computeIncremental({  
  iter: 40,  
  property: 'end',  
  onStep: function(perc){  
    Log.write(perc + '% loaded...');  
  },  
  onComplete: function(){  
    Log.write('done');  
    fd.animate({  
      modes: ['linear'],  
      transition: $jit.Trans.Elastic.easeOut,  
      duration: 2500  
    });  
  }  
});  
