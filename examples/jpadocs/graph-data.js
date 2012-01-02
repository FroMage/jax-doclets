var json = [
 {
  name: 'item',
  id: 'item',
  data: {
   $url: 'com/lunatech/doclets/jax/test/jpa/Item.html'
  },
  adjacencies: [
   {
    nodeTo: 'Order',
    data: {
     $labeltext: 'MANY..MANY',
     $type: 'fooType'
    }
   }
  ]
 }
 ,
 {
  name: 'Order',
  id: 'Order',
  data: {
   $url: 'com/lunatech/doclets/jax/test/jpa/Order.html'
  },
  adjacencies: [
   {
    nodeTo: 'Bill',
    data: {
     $labeltext: 'ONE..ONE',
     $type: 'fooType'
    }
   }
   ,
   {
    nodeTo: 'item',
    data: {
     $labeltext: 'MANY..MANY',
     $type: 'fooType'
    }
   }
   ,
   {
    nodeTo: 'Orderline',
    data: {
     $labeltext: 'ONE..MANY',
     $type: 'fooType'
    }
   }
   ,
   {
    nodeTo: 'Orderline',
    data: {
     $labeltext: 'ONE..MANY',
     $type: 'fooType'
    }
   }
  ]
 }
 ,
 {
  name: 'Orderline',
  id: 'Orderline',
  data: {
   $url: 'com/lunatech/doclets/jax/test/jpa/Orderline.html'
  },
  adjacencies: [
   {
    nodeTo: 'Order',
    data: {
     $labeltext: 'MANY..ONE',
     $type: 'fooType'
    }
   }
  ]
 }
 ,
 {
  name: 'Bill',
  id: 'Bill',
  data: {
   $url: 'com/lunatech/doclets/jax/test/jpa/Bill.html'
  },
  adjacencies: [
   {
    nodeTo: 'Order',
    data: {
     $labeltext: 'ONE..ONE',
     $type: 'fooType'
    }
   }
  ]
 }
];
