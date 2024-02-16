from scapy.all import *
from scapy.layers.inet import TCP, IP
from scapy.layers.l2 import getmacbyip, Ether, ARP
from mac_vendor_lookup import MacLookup

ip = get_if_addr(conf.iface)
print("The current globally routable IPv4 address is: ", ip, "\n")

mac = get_if_hwaddr(conf.iface)
print("The associated mac address is: ", mac, "\n")

ipv6 = conf.route6.route("fe80::/65")[1]
print("The current globally routable IPv6 address is: ", ipv6, "\n")

print("The name of the default gateway is: ", MacLookup().lookup(mac), "\n")

gw = conf.route.route("0.0.0.0")[2]
print("The IPv4 address of the main/default gateway is: ", gw, "\n")

gwmac = getmacbyip(gw)
print("The name of the default gateway is: ", MacLookup().lookup(gwmac), "\n")

arp = ARP(pdst="192.168.1.0/24")
broadcast = Ether(dst="ff:ff:ff:ff:ff:ff")
packet = broadcast / arp
ans, unans = srp(packet, timeout=2, verbose=0)
ans.summary(lambda s, r: r.sprintf("IP: %ARP.psrc%"))
print("The number of active/responsive hosts is: ", len(ans))

apple = 0
cisco = 0
for pkt in ans:
    company = MacLookup().lookup(pkt.answer.hwsrc)
    if "Apple" in company:
        apple += 1
    elif "Cisco" in company:
        cisco += 1

print("Number of Apple devices is: " + str(apple))
print("Number of Cisco devices is: " + str(cisco))

portnum = 0
syn = IP(dst="192.168.1.0/24") / TCP(dport=[80,443], flags="S")
ans, unans = sr(syn, timeout=2)
for sent, received in ans:
    if received[TCP].flags == "SA":
        portnum+=1

print("The number of devices with ports 80 and 443 open is: ",portnum)
