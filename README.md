## 1. How to run the kitchen service


## 2. Design Considerations
### 2.1 Key design consideration
* The preparation of the order and the courier's rush to the kitchen needs some time. We use DelayQueue to implement this.
* Different dispatch policy needs the different data structures to store the arrived courier for query efficiency. We bind the specific data structure with the DispatchPolicy.
* Courier needs to call some methods in some services. We still use Spring to manage the couriers for service injection automatically. The scope is prototype. Provider is used to create the courier instance.

### 2.2 Detailed design
1. OrderService
####API:
* receiveOrder: a. Call KitchenService::prepareOrder; b. Call CourierService::dispatchOrder
* deliverOrder: remove the order
####Structure:
* orders: HashMap

2. KitchenService
####API:
* prepareOrder: put the order into the preparingOrders
* pickOrder: a. remove the order from preparingOrders; b. Call DispatchPolicy::dispatch to get the Courier; c. Call Courier::deliverOrder
* arriveKitchen: Call DispatchPolicy
####Structure:
* preparingOrders: DelayedQueue

3. DispatchPolicy
####API:
* enQueue: put the Courier into waitingCourier
* dispatch: remove the courier from the waitingCourier
####Structure:
* waitingCourier: HashMap<OrderId, Courier>  Or List

4. CourierService
####API:
* rest:
* dispatchOrder: move one courier from idleCourier to onTheWayCourier
* rushToKitchen: move the courier from onTheWayCourier to waitingCourier(Call KitchenService::arriveKitchen)
####Structure:
* idleCourier: List
* onTheWayCourier: DelayedQueue

5. Courier
####API:
* deliverOrder: a. Call OrderService::deliverOrder; b. Call CourierService::rest

6. StatisticsService