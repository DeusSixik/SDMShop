# SDM Shop (7.10+)

## KubeJS
### Server Events `SDMShopEvents`
- buyEntry `(ShopBase shop, ShopEntry entry, ShopTab tab, ServerPlayer player, int count)`
- sellEntry `(ShopBase shop, ShopEntry entry, ShopTab tab, ServerPlayer player, int count)`
- shopChange `(ShopBase base)`
- entryAdd `(ShopBase shop, ShopEntry entry, ShopTab tab)`
- entryRemove `(ShopBase shop, ShopEntry entry, ShopTab tab)`
- entryChange `(ShopBase shop, ShopEntry entry, ShopTab tab)`
- tabAdd `(ShopBase shop, ShopTab tab)`
- tabRemove `(ShopBase shop, ShopTab tab)`
- tabChange `(ShopBase shop, ShopTab tab)`

### Modify ShopEntry

If you want to change any parameters in ShopEntry, you need to use the following structure.

```js
SDMShopEvents.buyEntry(event => {
    let entry = event.getEntry();
    let scriptData = entry.getScriptData();
    
    scriptData.setCount(scriptData.getCount() + 1)
    scriptData.setCount(scriptData.getCount() - 1)
})
```

> [!WARNING]
> The data you enter here will not be saved when you restart; it works as an offset value.

> [!CAUTION] 
> Similarly, if you change any data in ShopEntry or ShopTab, it may be saved, and the next time you log in, you will 
> have the latest data and will not be able to roll it back, unless you know its previous value.